package world.icebear03.splendidenchants.listener.caller

import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.util.isOffhand
import world.icebear03.splendidenchants.api.ItemAPI
import world.icebear03.splendidenchants.enchant.mechanism.EventType

object PlayerInteract {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun event(event: PlayerInteractEvent) {
        if (event.isOffhand())
            return

        if (!event.hasItem())
            return

        val player = event.player
        val itemInHand = player.inventory.itemInMainHand

        //以下是测试代码
        if (event.action == Action.RIGHT_CLICK_AIR ||
            event.action == Action.RIGHT_CLICK_BLOCK
        )
            ItemAPI.getEnchants(itemInHand).forEach {
                it.key.listeners.trigger(
                    event,
                    EventType.RIGHT_CLICK,
                    org.bukkit.event.EventPriority.HIGHEST,
                    player,
                    itemInHand
                )
            }
    }
}