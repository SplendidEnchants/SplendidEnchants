package world.icebear03.splendidenchants.listener.packet

import net.minecraft.world.item.ItemStack
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.nms.PacketSendEvent

object PacketWindowItems {

    @SubscribeEvent(priority = EventPriority.MONITOR)
    fun e(e: PacketSendEvent) {
        if (e.packet.name == "PacketPlayOutWindowItems") {
            val origin = e.packet.read<List<ItemStack>>("c")!!
            val c = origin.toMutableList()
            for (i in c.indices) {
//                val item=NMSImpl c[i]
//                c[i] = EnchantDisplayer.display(item,e.player)
            }
            e.packet.write("c", c)
        }
    }
}