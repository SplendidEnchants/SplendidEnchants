package world.icebear03.splendidenchants.ui

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.serverct.parrot.parrotx.function.variables
import org.serverct.parrot.parrotx.mechanism.Reloadable
import org.serverct.parrot.parrotx.ui.MenuComponent
import org.serverct.parrot.parrotx.ui.config.MenuConfiguration
import org.serverct.parrot.parrotx.ui.feature.util.MenuFunctionBuilder
import taboolib.module.chat.colored
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.isAir
import world.icebear03.splendidenchants.api.ItemAPI
import world.icebear03.splendidenchants.api.internal.YamlUpdater
import world.icebear03.splendidenchants.enchant.data.limitation.CheckType
import world.icebear03.splendidenchants.listener.mechanism.AnvilListener
import java.util.*

@MenuComponent("Anvil")
object AnvilUI {

    init {
        YamlUpdater.loadAndUpdate("gui/anvil.yml")
    }

    @Config("gui/anvil.yml")
    private lateinit var source: Configuration
    private lateinit var config: MenuConfiguration

    val dataItem = mutableMapOf<UUID, MutableMap<String, ItemStack?>>()
    val dataReason = mutableMapOf<UUID, Map<String, String>>()

    @Reloadable
    fun reload() {
        source.reload()
        config = MenuConfiguration(source)
    }

    fun open(player: Player) {
        if (!::config.isInitialized) {
            config = MenuConfiguration(source)
        }
        player.openMenu<Basic>(config.title().colored()) {
            virtualize()
            val (shape, templates) = config
            rows(shape.rows)
            map(*shape.array)

            onBuild { _, inventory ->
                shape.all(
                    "Anvil\$a", "Anvil\$b",
                    "Anvil\$result", "Anvil\$information",
                ) { slot, index, item, _ ->
                    inventory.setItem(slot, item(slot, index))
                }
            }

            val uuid = player.uniqueId
            if (!dataItem.containsKey(uuid))
                dataItem[uuid] = mutableMapOf()
            if (!dataReason.containsKey(uuid))
                dataReason[uuid] = mapOf()

            val a = dataItem[uuid]!!["a"]
            val b = dataItem[uuid]!!["b"]
            if (a != null && b != null) {
                val resultPair = AnvilListener.anvil(a as ItemStack, b as ItemStack, player)
                val result = resultPair.first
                val levelCost = AnvilListener.finalCost(resultPair.second, player)

                var flag = true
                if (result == null || levelCost <= 0) {
                    flag = false
                } else {
                    if (result.isSimilar(a)) {
                        flag = false
                    }
                }
                if (flag) {
                    dataItem[uuid]!!["result"] = result
                    dataReason[uuid] =
                        mapOf(
                            "allowed" to "&a允许",
                            "level" to "${levelCost}",
                            "reasons" to "无"
                        )
                } else {
                    dataItem[uuid]!!["result"] = null
                    val bugs = mutableListOf<String>()
                    ItemAPI.getEnchants(b).forEach {
                        val enchant = it.key
                        val checkPair = enchant.limitations.checkAvailable(CheckType.ANVIL, player, a)
                        if (!checkPair.first)
                            bugs += checkPair.second
                    }
                    dataReason[uuid] =
                        mapOf(
                            "allowed" to "&c不允许",
                            "level" to "N/A",
                            "reasons" to bugs.also {
                                listOf("", "其他可能:", "拼合前后物品不变", "经验值消耗小于等于0级")
                            }.joinToString("\$\$")
                        )
                }
            } else {
                dataItem[uuid]!!["result"] = null
                dataReason[uuid] = mapOf()
            }

            val tmp = listOf("a", "b", "result", "information")
            tmp.forEach {
                shape["Anvil\$$it"].first().let { slot ->
                    set(slot, templates("Anvil\$$it", slot, 0, false, "Fallback", uuid))
                }
            }

            onClick { event ->
                event.isCancelled = true
                if (event.rawSlot in shape) {
                    templates[event.rawSlot]?.handle(event)
                }
                if (event.rawSlot !in shape) {
                    val item = event.virtualEvent().clickItem
                    if (item.isAir)
                        return@onClick
                    if (a == null) {
                        dataItem[player.uniqueId]!!["a"] = item
                        open(player)
                        return@onClick
                    }
                    if (b == null) {
                        dataItem[player.uniqueId]!!["b"] = item
                        open(player)
                    }
                }
            }
        }
    }

    @MenuComponent
    private val a = MenuFunctionBuilder {
        onBuild { (_, _, _, _, icon, args) ->
            dataItem[args[0]]!!["a"] ?: icon
        }
        onClick { (_, _, event, _) ->
            val player = event.clicker
            dataItem[player.uniqueId]!!.remove("a")
            open(player)
        }
    }

    @MenuComponent
    private val b = MenuFunctionBuilder {
        onBuild { (_, _, _, _, icon, args) ->
            dataItem[args[0]]!!["b"] ?: icon
        }
        onClick { (_, _, event, _) ->
            val player = event.clicker
            dataItem[player.uniqueId]!!.remove("b")
            open(player)
        }
    }

    @MenuComponent
    private val result = MenuFunctionBuilder {
        onBuild { (_, _, _, _, icon, args) ->
            dataItem[args[0]]!!["result"] ?: icon
        }
        onClick { (_, _, event, _) ->

        }
    }

    @MenuComponent
    private val information = MenuFunctionBuilder {
        onBuild { (_, _, _, _, icon, args) ->
            val map = dataReason[args[0]]!!
            if (map.isEmpty()) {
                icon.variables {
                    when (it) {
                        "allowed" -> listOf("-")
                        "level" -> listOf("-")
                        "reasons" -> listOf("-")
                        else -> listOf()
                    }
                }
            } else {
                icon.variables {
                    when (it) {
                        "allowed" -> listOf(map["allowed"]!!)
                        "level" -> listOf(map["level"]!!)
                        "reasons" -> map["reasons"]!!.split("\$\$")
                        else -> listOf()
                    }
                }
            }
        }
        onClick { (_, _, event, _) ->

        }
    }
}