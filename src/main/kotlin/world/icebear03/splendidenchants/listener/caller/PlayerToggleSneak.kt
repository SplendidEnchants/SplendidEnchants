package world.icebear03.splendidenchants.listener.caller

import org.bukkit.event.player.PlayerToggleSneakEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import world.icebear03.splendidenchants.api.internal.TriggerSlots
import world.icebear03.splendidenchants.api.triggerEts
import world.icebear03.splendidenchants.enchant.mechanism.EventType

object PlayerToggleSneak {

    @SubscribeEvent(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun lowest(event: PlayerToggleSneakEvent) = settle(event, EventPriority.LOWEST)

    @SubscribeEvent(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun high(event: PlayerToggleSneakEvent) = settle(event, EventPriority.HIGH)

    @SubscribeEvent(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun highest(event: PlayerToggleSneakEvent) = settle(event, EventPriority.HIGHEST)

    private fun settle(event: PlayerToggleSneakEvent, priority: EventPriority) {
        if (!event.player.isSneaking)
            EventType.SNEAK.triggerEts(event, priority, TriggerSlots.ALL, event.player)
    }
}