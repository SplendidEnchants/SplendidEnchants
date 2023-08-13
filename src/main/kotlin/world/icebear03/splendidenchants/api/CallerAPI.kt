package world.icebear03.splendidenchants.api

import org.bukkit.entity.Player
import org.bukkit.event.Event
import taboolib.common.platform.event.EventPriority
import world.icebear03.splendidenchants.enchant.mechanism.EventType
import world.icebear03.splendidenchants.util.TriggerSlots

fun EventType.triggerEts(
    event: Event,
    priority: EventPriority,
    slots: TriggerSlots,
    player: Player
) {
    val inventory = player.inventory
    slots.slots.forEach {
        val item = inventory.getItem(it)

        item.fixedEnchants.forEach { enchantPair ->
            enchantPair.key.listeners.trigger(
                event,
                this,
                priority,
                player,
                item
            )
        }
    }
}