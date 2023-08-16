package world.icebear03.splendidenchants.listener.caller

import org.bukkit.Material
import org.bukkit.event.block.Action.*
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.util.isMainhand
import world.icebear03.splendidenchants.api.internal.TriggerSlots
import world.icebear03.splendidenchants.api.triggerEts
import world.icebear03.splendidenchants.enchant.mechanism.EventType

object PlayerInteract {

    @SubscribeEvent(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun lowest(event: PlayerInteractEvent) = settle(event, EventPriority.LOWEST)

    @SubscribeEvent(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun high(event: PlayerInteractEvent) = settle(event, EventPriority.HIGH)

    @SubscribeEvent(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun highest(event: PlayerInteractEvent) = settle(event, EventPriority.HIGHEST)

    @SubscribeEvent(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun lowest(event: PlayerInteractEntityEvent) = settle(event, EventPriority.LOWEST)

    @SubscribeEvent(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun high(event: PlayerInteractEntityEvent) = settle(event, EventPriority.HIGH)

    @SubscribeEvent(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun highest(event: PlayerInteractEntityEvent) = settle(event, EventPriority.HIGHEST)

    private fun settle(event: PlayerInteractEntityEvent, priority: EventPriority) {
        val player = event.player
        val hand = if (event.isMainhand()) TriggerSlots.MAIN_HAND else TriggerSlots.OFF_HAND

        if (player.inventory.itemInMainHand.type == Material.AIR)
            return

        EventType.RIGHT_CLICK.triggerEts(event, priority, hand, player)
        EventType.INTERACT_ENTITY.triggerEts(event, priority, hand, player)
    }

    private fun settle(event: PlayerInteractEvent, priority: EventPriority) {
        if (!event.hasItem()) return

        val player = event.player
        val hand = if (event.isMainhand()) TriggerSlots.MAIN_HAND else TriggerSlots.OFF_HAND

        when (event.action) {
            RIGHT_CLICK_BLOCK, RIGHT_CLICK_AIR ->
                EventType.RIGHT_CLICK.triggerEts(event, priority, hand, player)

            LEFT_CLICK_BLOCK, LEFT_CLICK_AIR ->
                EventType.LEFT_CLICK.triggerEts(event, priority, hand, player)

            PHYSICAL -> {}
        }
    }
}