package world.icebear03.splendidenchants.listener.packet

import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.nms.PacketSendEvent
import taboolib.module.nms.nmsProxy
import world.icebear03.splendidenchants.api.internal.nms.NMS

object PacketOpenWindowMerchant {

    @SubscribeEvent(priority = EventPriority.MONITOR)
    fun e(e: PacketSendEvent) {
        if (e.packet.name == "PacketPlayOutOpenWindowMerchant") {
            val merchant = e.packet.read<Any>("b")!!
            e.packet.write("b", nmsProxy<NMS>().adaptMerchantRecipe(merchant, e.player))
        }
    }
}