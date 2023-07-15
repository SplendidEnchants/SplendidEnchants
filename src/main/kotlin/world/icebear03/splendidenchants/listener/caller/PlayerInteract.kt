package world.icebear03.splendidenchants.listener.caller

import org.bukkit.Material
import org.bukkit.event.block.Action.*
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.util.isOffhand
import world.icebear03.splendidenchants.api.CallerAPI
import world.icebear03.splendidenchants.enchant.mechanism.EventType
import world.icebear03.splendidenchants.util.TriggerSlots

object PlayerInteract {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun eventLowest(event: PlayerInteractEvent) {
        settle(event, EventPriority.LOWEST)
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    fun eventHigh(event: PlayerInteractEvent) {
        settle(event, EventPriority.HIGH)
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun eventHighest(event: PlayerInteractEvent) {
        settle(event, EventPriority.HIGHEST)
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun eventLowest(event: PlayerInteractEntityEvent) {
        settle(event, EventPriority.LOWEST)
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    fun eventHigh(event: PlayerInteractEntityEvent) {
        settle(event, EventPriority.HIGH)
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun eventHighest(event: PlayerInteractEntityEvent) {
        settle(event, EventPriority.HIGHEST)
    }

    private fun settle(event: PlayerInteractEntityEvent, priority: EventPriority) {
        if (event.isOffhand())
            return

        val player = event.player

        if (player.inventory.itemInMainHand.type == Material.AIR)
            return

        //仍然算作右击
        CallerAPI.trigger(
            event,
            EventType.RIGHT_CLICK,
            priority,
            TriggerSlots.MAIN_HAND,
            player
        )

        //右击生物
        CallerAPI.trigger(
            event,
            EventType.INTERACT_ENTITY,
            priority,
            TriggerSlots.MAIN_HAND,
            player
        )
    }

    private fun settle(event: PlayerInteractEvent, priority: EventPriority) {
        if (event.isOffhand())
            return

        if (!event.hasItem())
            return

        val player = event.player

        when (event.action) {
            RIGHT_CLICK_BLOCK, RIGHT_CLICK_AIR -> CallerAPI.trigger(
                event,
                EventType.RIGHT_CLICK,
                priority,
                TriggerSlots.MAIN_HAND,
                player
            )

            LEFT_CLICK_BLOCK, LEFT_CLICK_AIR -> CallerAPI.trigger(
                event,
                EventType.LEFT_CLICK,
                priority,
                TriggerSlots.MAIN_HAND,
                player
            )

            PHYSICAL -> {}//NONE
        }
    }
}