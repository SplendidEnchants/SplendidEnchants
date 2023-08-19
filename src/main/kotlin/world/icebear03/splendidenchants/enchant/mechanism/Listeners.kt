package world.icebear03.splendidenchants.enchant.mechanism

import org.bukkit.entity.LivingEntity
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.EventPriority
import taboolib.library.configuration.ConfigurationSection
import world.icebear03.splendidenchants.api.etLevel
import world.icebear03.splendidenchants.enchant.SplendidEnchant
import world.icebear03.splendidenchants.enchant.data.limitation.CheckType
import world.icebear03.splendidenchants.enchant.mechanism.chain.Chain

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
    ) {
        if (!enchant.limitations.checkAvailable(CheckType.USE, item, entity).first) return
        byType[eventType]?.filter { byId[it]!!.first == priority }?.forEach listeners@{ id ->
            val holders = mutableMapOf<String, Any>()
            byId[id]!!.second.forEach chains@{ chain ->
                holders["随机数"] = Math.random() * 100
                holders += enchant.variable.variables(item.etLevel(enchant), entity, item)
                if (!chain.trigger(event, eventType, entity, item, holders)) return@listeners
            }
        }
    }
}