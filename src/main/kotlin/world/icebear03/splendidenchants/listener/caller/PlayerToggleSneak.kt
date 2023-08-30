package world.icebear03.splendidenchants.listener.caller

import org.bukkit.event.player.PlayerToggleSneakEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import world.icebear03.splendidenchants.api.internal.TriggerSlots
import world.icebear03.splendidenchants.api.triggerEts
import world.icebear03.splendidenchants.enchant.mechanism.EventType

object PlayerToggleSneak {

    @SubscribeEvent(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun highest(event: PlayerToggleSneakEvent) = settle(event)

    private fun settle(event: PlayerToggleSneakEvent) {
        if (!event.player.isSneaking)
            EventType.SNEAK.triggerEts(event, EventPriority.HIGHEST, TriggerSlots.ALL, event.player)
    }
}