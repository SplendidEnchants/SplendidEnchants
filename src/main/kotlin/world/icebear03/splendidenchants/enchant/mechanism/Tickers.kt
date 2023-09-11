package world.icebear03.splendidenchants.enchant.mechanism

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.common.platform.service.PlatformExecutor
import taboolib.library.configuration.ConfigurationSection
import taboolib.platform.util.onlinePlayers
import world.icebear03.splendidenchants.api.calcToDouble
import world.icebear03.splendidenchants.api.calcToInt
import world.icebear03.splendidenchants.api.etLevel
import world.icebear03.splendidenchants.api.isNull
import world.icebear03.splendidenchants.enchant.SplendidEnchant
import world.icebear03.splendidenchants.enchant.data.limitation.CheckType
import world.icebear03.splendidenchants.enchant.mechanism.Tickers.Companion.Mode.*
import world.icebear03.splendidenchants.enchant.mechanism.chain.Chain
import world.icebear03.splendidenchants.enchant.mechanism.chain.ChainType
import world.icebear03.splendidenchants.enchant.mechanism.entry.internal.ObjectEntry
import world.icebear03.splendidenchants.enchant.mechanism.entry.internal.objPlayer
import java.util.*
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class Tickers(val enchant: SplendidEnchant, config: ConfigurationSection?) {

    val byId = mutableMapOf<String, List<Chain>>()
    val beforeById = mutableMapOf<String, List<Chain>>()
    val afterById = mutableMapOf<String, List<Chain>>()

    init {
        config?.getKeys(false)?.forEach { id ->
            val interval = config.getInt("$id.interval", 20)
            byId[id] = config.getStringList("$id.chains").map { Chain(enchant, it) }
            beforeById[id] = config.getStringList("$id.chains_before").map { Chain(enchant, it) }
            afterById[id] = config.getStringList("$id.chains_after").map { Chain(enchant, it) }
            routine[enchant to "${enchant.basicData.id}.$id"] = interval
        }
    }

    fun trigger(player: Player, item: ItemStack, mode: Mode) {
        when (mode) {
            BEFORE -> beforeById
            AFTER -> afterById
            NORMAL -> byId
        }.forEach { (_, chains) ->
            val sHolders = mutableMapOf<String, String>()
            val fHolders = mutableMapOf<String, Pair<ObjectEntry<*>, String>>()

            fun next(tot: Int = 0) {
                if (tot >= chains.size) return
                val chain = chains[tot]
                sHolders["随机数"] = (Math.random() * 100).roundToInt().toString()
                sHolders += enchant.variable.variables(item.etLevel(enchant), player, item, false)
                fHolders["玩家"] = objPlayer.h(player)

                if (chain.type == ChainType.DELAY) submit(delay = (chain.content.calcToDouble(sHolders) * 20).roundToLong()) { next(tot + 1) }
                else if (chain.type == ChainType.GOTO) next(chain.content.calcToInt(sHolders) - 1)
                else if (chain.trigger(null, null, player, item, sHolders, fHolders)) next(tot + 1)
            }
            next()
        }
    }

    companion object {

        val routine = mutableMapOf<Pair<SplendidEnchant, String>, Int>()

        val recorder = mutableMapOf<UUID, MutableSet<String>>()

        var task: PlatformExecutor.PlatformTask? = null

        enum class Mode {
            BEFORE,
            AFTER,
            NORMAL
        }

        @SubscribeEvent
        fun quit(event: PlayerQuitEvent) {
            recorder.remove(event.player.uniqueId)
        }

        fun load() {
            routine.clear()

            var counter = 0
            println(task)
            task?.cancel()
            println(task)
            task = submit(period = 1L) {
                counter++
                routine.filterValues { counter % it == 0 }.forEach { (pair, _) ->
                    val enchant = pair.first
                    val id = pair.second
                    val slots = enchant.targets.flatMap { it.activeSlots }.toSet()
                    onlinePlayers.forEach { player ->
                        var flag = false
                        val set = recorder.getOrPut(player.uniqueId) { mutableSetOf() }
                        slots.forEach slot@{ slot ->
                            val item = player.inventory.getItem(slot)
                            if (item.isNull) return@slot
                            if (item.etLevel(enchant) > 0) {
                                if (!enchant.limitations.checkAvailable(CheckType.USE, item, player, slot).first) return@slot
                                flag = true

                                if (!set.contains(id)) {
                                    set += pair.second
                                    enchant.tickers.trigger(player, item, BEFORE)
                                }
                                enchant.tickers.trigger(player, item, NORMAL)
                            }
                        }
                        if (!flag && set.contains(id)) {
                            set -= pair.second
                            enchant.tickers.trigger(player, ItemStack(Material.STONE), AFTER)
                        }
                    }
                }
            }
        }
    }
}