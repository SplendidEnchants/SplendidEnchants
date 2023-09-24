package world.icebear03.splendidenchants.listener.caller

import org.bukkit.event.player.PlayerChatEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import world.icebear03.splendidenchants.api.internal.TriggerSlots
import world.icebear03.splendidenchants.api.triggerEts
import world.icebear03.splendidenchants.enchant.mechanism.EventType

object PlayerChat {
    @SubscribeEvent(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun highest(event: PlayerChatEvent) = EventType.CHAT.triggerEts(event, EventPriority.HIGHEST, TriggerSlots.ALL, event.player)
}
