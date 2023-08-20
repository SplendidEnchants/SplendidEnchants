package world.icebear03.splendidenchants.listener.caller

import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDamageEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import world.icebear03.splendidenchants.api.internal.TriggerSlots
import world.icebear03.splendidenchants.api.triggerEts
import world.icebear03.splendidenchants.enchant.mechanism.EventType

object EntityDamage {

    @SubscribeEvent(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun lowest(event: EntityDamageEvent) = settle(event, EventPriority.LOWEST)

    @SubscribeEvent(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun high(event: EntityDamageEvent) = settle(event, EventPriority.HIGH)

    @SubscribeEvent(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun highest(event: EntityDamageEvent) = settle(event, EventPriority.HIGHEST)

    private fun settle(event: EntityDamageEvent, priority: EventPriority) {
        EventType.DAMAGED.triggerEts(event, priority, TriggerSlots.ALL, event.entity as? LivingEntity ?: return)
    }
}