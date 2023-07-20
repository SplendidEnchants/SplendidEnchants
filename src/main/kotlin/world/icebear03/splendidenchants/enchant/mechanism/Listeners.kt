package world.icebear03.splendidenchants.enchant.mechanism

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.EventPriority
import taboolib.library.configuration.ConfigurationSection
import world.icebear03.splendidenchants.api.ItemAPI
import world.icebear03.splendidenchants.enchant.SplendidEnchant
import world.icebear03.splendidenchants.enchant.data.limitation.CheckType
import world.icebear03.splendidenchants.enchant.mechanism.chain.Chain
import java.util.concurrent.ConcurrentHashMap

data class Listeners(val enchant: SplendidEnchant, val config: ConfigurationSection?) {

    var belonging: SplendidEnchant = enchant

    //一个listenerId指向一个listener
    val listenersById = ConcurrentHashMap<String, Pair<EventPriority, List<Chain>>>()

    //一个eventType指向所有该type的listeners的ids
    val listenersByType = ConcurrentHashMap<EventType, MutableList<String>>()

    init {
        config?.getKeys(false)?.forEach {
            val id = it
            val type = EventType.valueOf(config.getString("$id.type", "")!!)
            val priority = EventPriority.valueOf(config.getString("$id.priority", "HIGHEST")!!)
            val chainLines = config.getStringList("$id.chains")

            val chains = mutableListOf<Chain>()
            chainLines.forEach { chainLine ->
                chains += Chain(this, chainLine)
            }

            listenersById[id] = priority to chains
            if (!listenersByType.contains(type))
                listenersByType[type] = mutableListOf()
            listenersByType[type]!! += id
        }
    }

    fun trigger(
        event: Event,
        eventType: EventType,
        eventPriority: EventPriority,
        player: Player,
        item: ItemStack,
    ) {
        if (!belonging.limitations.checkAvailable(CheckType.USE, player, item).first)
            return

        listenersByType[eventType]?.forEach {
            if (listenersById[it]!!.first == eventPriority) {

                val replacerMap =
                    belonging.variable.generateReplaceMap(
                        ItemAPI.getLevel(item, belonging),
                        player,
                        item
                    )

                listenersById[it]!!.second.forEach { chain ->
//                    player.sendMessage(chain.chainLine)
                    val canContinue = chain.trigger(event, eventType, player, item, replacerMap)
                    if (!canContinue)
                        return

                    val refreshed = belonging.variable.generateReplaceMap(
                        ItemAPI.getLevel(item, belonging),
                        player,
                        item
                    )
                    refreshed.forEach {
                        replacerMap.removeIf { origin -> it.first == origin.first }
                    }
                    replacerMap.addAll(refreshed)
                }
            }
        }
    }
}