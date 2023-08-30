package world.icebear03.splendidenchants.listener.caller

import org.bukkit.event.player.PlayerToggleFlightEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import world.icebear03.splendidenchants.api.internal.TriggerSlots
import world.icebear03.splendidenchants.api.triggerEts
import world.icebear03.splendidenchants.enchant.mechanism.EventType

object PlayerToggleFlight {

    @SubscribeEvent(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun highest(event: PlayerToggleFlightEvent) = settle(event)

    private fun settle(event: PlayerToggleFlightEvent) {
        if (!event.player.isFlying)
            EventType.FLY.triggerEts(event, EventPriority.HIGHEST, TriggerSlots.ALL, event.player)
    }
}