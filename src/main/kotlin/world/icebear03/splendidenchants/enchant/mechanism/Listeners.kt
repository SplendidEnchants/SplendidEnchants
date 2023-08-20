package world.icebear03.splendidenchants.enchant.mechanism

import org.bukkit.entity.LivingEntity
import org.bukkit.event.Event
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.function.submit
import taboolib.common5.cdouble
import taboolib.common5.cint
import taboolib.library.configuration.ConfigurationSection
import world.icebear03.splendidenchants.api.calculate
import world.icebear03.splendidenchants.api.etLevel
import world.icebear03.splendidenchants.enchant.SplendidEnchant
import world.icebear03.splendidenchants.enchant.data.limitation.CheckType
import world.icebear03.splendidenchants.enchant.mechanism.chain.Chain
import world.icebear03.splendidenchants.enchant.mechanism.chain.ChainType
import kotlin.math.roundToLong

class Listeners(val enchant: SplendidEnchant, config: ConfigurationSection?) {

    //一个listener就是 list<chain>
    //一个listenerId指向一个listener
    val byId = mutableMapOf<String, Pair<EventPriority, List<Chain>>>()

    //一个eventType指向所有该type的listeners的ids
    val byType = mutableMapOf<EventType, MutableList<String>>()

    init {
        config?.getKeys(false)?.forEach { id ->
            val type = EventType.getType(config.getString("$id.type")) ?: return@forEach
            val priority = EventPriority.entries.find { it.name == config.getString("$id.priority", "HIGHEST") } ?: return@forEach
            val lines = config.getStringList("$id.chains")
            val chains = lines.map { Chain(this, it) }
            byId[id] = priority to chains
            byType.getOrPut(type) { mutableListOf() } += id
        }
    }

    fun trigger(
        event: Event,
        eventType: EventType,
        priority: EventPriority,
        entity: LivingEntity,
        item: ItemStack,
        slot: EquipmentSlot
    ) {
        if (!enchant.limitations.checkAvailable(CheckType.USE, item, entity, slot).first) return

        byType[eventType]?.filter { byId[it]!!.first == priority }?.forEach listeners@{ id ->
            val holders = mutableMapOf<String, Any>()
            holders += enchant.variable.flexible
            val chains = byId[id]!!.second
            fun next(tot: Int = 0) {
                if (tot >= chains.size)
                    return
                holders["随机数"] = Math.random() * 100
                holders += enchant.variable.variables(item.etLevel(enchant), entity, item)
                val chain = chains[tot]
                if (chain.type == ChainType.DELAY) {
                    val ticks = chain.content.calculate(holders).cdouble
                    submit(delay = (ticks * 20).roundToLong()) { next(tot + 1) }
                } else if (chain.type == ChainType.GOTO) {
                    val index = chain.content.calculate(holders).cint
                    next(index - 1)
                } else if (chain.trigger(event, eventType, entity, item, holders, tot == 0)) {
                    next(tot + 1)
                }
            }
            next()
        }
    }
}