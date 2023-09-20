package world.icebear03.splendidenchants.listener.caller

import org.bukkit.event.player.AsyncPlayerChatEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import world.icebear03.splendidenchants.api.internal.TriggerSlots
import world.icebear03.splendidenchants.api.triggerEts
import world.icebear03.splendidenchants.enchant.mechanism.EventType

object PlayerChat {

    @SubscribeEvent(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun high(event: AsyncPlayerChatEvent) = settle(event, EventPriority.HIGH)

    @SubscribeEvent(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun highest(event: AsyncPlayerChatEvent) = settle(event, EventPriority.HIGHEST)

    @SubscribeEvent(priority = EventPriority.LOW, ignoreCancelled = true)
    fun low(event: AsyncPlayerChatEvent) = settle(event, EventPriority.LOW)

    @SubscribeEvent(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun lowest(event: AsyncPlayerChatEvent) = settle(event, EventPriority.LOWEST)

    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun monitor(event: AsyncPlayerChatEvent) = settle(event, EventPriority.MONITOR)

    @SubscribeEvent(ignoreCancelled = true)
    fun normal(event: AsyncPlayerChatEvent) = settle(event)

    private fun settle(event: AsyncPlayerChatEvent, priority: EventPriority = EventPriority.NORMAL) {
        EventType.CHAT.triggerEts(event, priority, TriggerSlots.ALL, event.player)
    }
}
