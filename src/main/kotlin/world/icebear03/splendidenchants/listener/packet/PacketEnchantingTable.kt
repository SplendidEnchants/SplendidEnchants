package world.icebear03.splendidenchants.listener.packet

import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.nms.PacketSendEvent

object PacketEnchantingTable {

    @SubscribeEvent(priority = EventPriority.MONITOR)
    fun e(e: PacketSendEvent) {
        if (e.packet.name == "PacketPlayOutWindowData") {
            val a = e.packet.read<Int>("b", false)
            if (a in 4..6) {
                e.packet.write("c", -1)
            }
        }
    }
}