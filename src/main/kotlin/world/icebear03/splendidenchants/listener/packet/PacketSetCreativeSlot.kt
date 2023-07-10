package world.icebear03.splendidenchants.listener.packet

import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.PacketReceiveEvent
import taboolib.platform.util.isAir
import world.icebear03.splendidenchants.api.nms.NMS
import world.icebear03.splendidenchants.api.nms.NMS16ItemStack
import world.icebear03.splendidenchants.api.nms.NMSItemStack
import world.icebear03.splendidenchants.enchant.EnchantDisplayer

object PacketSetCreativeSlot {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun e(e: PacketReceiveEvent) {
        if (e.packet.name == "PacketPlayInSetCreativeSlot") {
            when (MinecraftVersion.major) {
                // 1.16 -> b
                8 -> {
                    val origin = e.packet.read<NMS16ItemStack>("b")!!
                    val bkItem = NMS.INSTANCE.toBukkitItemStack(origin)
                    if (bkItem.isAir) return
                    val adapted =
                        NMS.INSTANCE.toNMSItemStack(EnchantDisplayer.undisplay(bkItem, e.player)) as NMS16ItemStack
                    e.packet.write("b", adapted)
                }
                // (1.17, 1.18)?, 1.19, 1.20 -> b
                in 9..12 -> {
                    val origin = e.packet.read<NMSItemStack>("b")!!
                    val bkItem = NMS.INSTANCE.toBukkitItemStack(origin)
                    if (bkItem.isAir) return
                    val adapted =
                        NMS.INSTANCE.toNMSItemStack(EnchantDisplayer.undisplay(bkItem, e.player)) as NMSItemStack
                    e.packet.write("b", adapted)
                }
                else -> error("Unsupported version.")
            }
        }
    }
}