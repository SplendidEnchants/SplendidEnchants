package world.icebear03.splendidenchants.listener.packet

import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.PacketSendEvent
import taboolib.platform.util.isAir
import world.icebear03.splendidenchants.api.nms.NMS
import world.icebear03.splendidenchants.api.nms.NMS16ItemStack
import world.icebear03.splendidenchants.api.nms.NMSItemStack
import world.icebear03.splendidenchants.enchant.EnchantDisplayer

object PacketSetSlot {

    @SubscribeEvent(priority = EventPriority.MONITOR)
    fun e(e: PacketSendEvent) {
        if (e.packet.name == "PacketPlayOutSetSlot") {
            when (MinecraftVersion.major) {
                // 1.16 -> c
                8 -> {
                    val origin = e.packet.read<NMS16ItemStack>("c")!!
                    val bkItem = NMS.INSTANCE.toBukkitItemStack(origin)
                    if (bkItem.isAir) return
                    val adapted =
                        NMS.INSTANCE.toNMSItemStack(EnchantDisplayer.display(bkItem, e.player)) as NMS16ItemStack
                    e.packet.write("c", adapted)
                }
                // (1.17, 1.18)?, 1.19, 1.20 -> f
                in 9..12 -> {
                    val origin = e.packet.read<NMSItemStack>("f")!!
                    val bkItem = NMS.INSTANCE.toBukkitItemStack(origin)
                    if (bkItem.isAir) return
                    val adapted =
                        NMS.INSTANCE.toNMSItemStack(EnchantDisplayer.display(bkItem, e.player)) as NMSItemStack
                    e.packet.write("f", adapted)
                }
                else -> error("Unsupported version.")
            }
        }
    }
}