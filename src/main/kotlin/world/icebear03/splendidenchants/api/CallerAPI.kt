package world.icebear03.splendidenchants.api

import org.bukkit.entity.LivingEntity
import org.bukkit.event.Event
import taboolib.common.platform.event.EventPriority
import world.icebear03.splendidenchants.api.internal.TriggerSlots
import world.icebear03.splendidenchants.enchant.mechanism.EventType

fun EventType.triggerEts(
    event: Event,
    priority: EventPriority,
    slots: TriggerSlots,
    entity: LivingEntity
) {
    val inventory = entity.equipment ?: return
    slots.slots.forEach {
        val item = inventory.getItem(it)
        if (item.isNull) return@forEach

        item.fixedEnchants.forEach { enchantPair ->
            enchantPair.key.listeners.trigger(
                event,
                this,
                priority,
                entity,
                item,
                it
            )
        }
    }
}