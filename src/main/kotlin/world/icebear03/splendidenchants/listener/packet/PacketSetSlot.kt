package world.icebear03.splendidenchants.listener.packet

import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.PacketSendEvent
import taboolib.platform.util.isAir
import world.icebear03.splendidenchants.api.nms.NMS
import world.icebear03.splendidenchants.enchant.EnchantDisplayer

object PacketSetSlot {

    @SubscribeEvent(priority = EventPriority.MONITOR)
    fun e(e: PacketSendEvent) {
        if (e.packet.name == "PacketPlayOutSetSlot") {
            val field = when (MinecraftVersion.major) {
                8 -> "c" // 1.16 -> b
                in 9..12 -> "f" // 1.17, 1.18, 1.19, 1.20 -> c
                else -> error("Unsupported version.") // Unsupported
            }
            val origin = e.packet.read<Any>(field)!!
            val bkItem = NMS.INSTANCE.toBukkitItemStack(origin)
            if (bkItem.isAir) return
            val adapted = NMS.INSTANCE.toNMSItemStack(EnchantDisplayer.display(bkItem, e.player))
            e.packet.write(field, adapted)
        }
    }
}