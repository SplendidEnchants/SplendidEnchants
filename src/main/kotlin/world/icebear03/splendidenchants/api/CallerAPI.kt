package world.icebear03.splendidenchants.api

import org.bukkit.entity.Player
import org.bukkit.event.Event
import taboolib.common.platform.event.EventPriority
import world.icebear03.splendidenchants.enchant.mechanism.EventType
import world.icebear03.splendidenchants.util.TriggerSlots

object CallerAPI {

    fun trigger(
        event: Event,
        type: EventType,
        priority: EventPriority,
        slots: TriggerSlots,
        player: Player
    ) {
        val inventory = player.inventory
        slots.slots.forEach {
            val item = inventory.getItem(it)

            ItemAPI.getEnchants(item).forEach { enchantPair ->
                enchantPair.key.listeners.trigger(
                    event,
                    type,
                    priority,
                    player,
                    item
                )
            }
        }
    }
}