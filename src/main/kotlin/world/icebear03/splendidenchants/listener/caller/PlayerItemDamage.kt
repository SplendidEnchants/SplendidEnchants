package world.icebear03.splendidenchants.listener.caller

import org.bukkit.event.player.PlayerItemDamageEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import world.icebear03.splendidenchants.api.internal.TriggerSlots
import world.icebear03.splendidenchants.api.triggerEts
import world.icebear03.splendidenchants.enchant.mechanism.EventType

object PlayerItemDamage {

    @SubscribeEvent(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun highest(event: PlayerItemDamageEvent) = settle(event)

    private fun settle(event: PlayerItemDamageEvent) {
        EventType.DURABILITY_REDUCED.triggerEts(event, EventPriority.HIGHEST, TriggerSlots.ALL, event.player)
    }
}