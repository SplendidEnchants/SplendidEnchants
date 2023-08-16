package world.icebear03.splendidenchants.listener.packet

import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.nms.NMSItem
import taboolib.module.nms.PacketReceiveEvent
import taboolib.platform.util.isAir
import world.icebear03.splendidenchants.enchant.EnchantDisplayer

object PacketSetCreativeSlot {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun e(e: PacketReceiveEvent) {
        if (e.packet.name == "PacketPlayInSetCreativeSlot") {
            val origin = e.packet.read<Any>("b")!!
            val bkItem = NMSItem.asBukkitCopy(origin)
            if (bkItem.isAir) return
            val adapted = NMSItem.asNMSCopy(EnchantDisplayer.undisplay(bkItem, e.player))
            e.packet.write("b", adapted)
        }
    }
}