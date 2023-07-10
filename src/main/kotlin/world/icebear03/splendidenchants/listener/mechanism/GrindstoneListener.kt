package world.icebear03.splendidenchants.listener.mechanism

import org.bukkit.event.inventory.PrepareGrindstoneEvent
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent


object GrindstoneListener {

    @SubscribeEvent(priority = EventPriority.LOW)

    fun event(event: PrepareGrindstoneEvent) {
        val inv = event.inventory
        val upper: ItemStack? = inv.upperItem
        val lower: ItemStack? = inv.lowerItem
//        if (!ItemUtils.grindstoneable(upper) || !ItemUtils.grindstoneable(lower)) {
//            grindstone.setItem(2, ItemStack(Material.AIR))
//            event.setCancelled(true)
//        }
    }
}