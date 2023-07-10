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

object PacketWindowItems {

    @SubscribeEvent(priority = EventPriority.MONITOR)
    fun e(e: PacketSendEvent) {
        if (e.packet.name == "PacketPlayOutWindowItems") {
            when (MinecraftVersion.major) {
                // 1.16 -> b
                8 -> {
                    val slots = e.packet.read<List<NMS16ItemStack>>("b")!!.toMutableList()
                    for (i in slots.indices) {
                        val bkItem = NMS.INSTANCE.toBukkitItemStack(slots[i])
                        if (bkItem.isAir) continue
                        val nmsItem = NMS.INSTANCE.toNMSItemStack(EnchantDisplayer.display(bkItem, e.player))
                        slots[i] = nmsItem as NMS16ItemStack
                    }
                    e.packet.write("b", slots.toList())
                }
                // 1.17, 1.18, 1.19, 1.20 -> c
                9, 10, 11, 12 -> {
                    val slots = e.packet.read<List<NMSItemStack>>("c")!!.toMutableList()
                    for (i in slots.indices) {
                        val bkItem = NMS.INSTANCE.toBukkitItemStack(slots[i])
                        if (bkItem.isAir) continue
                        val nmsItem = NMS.INSTANCE.toNMSItemStack(EnchantDisplayer.display(bkItem, e.player))
                        slots[i] = nmsItem as NMSItemStack
                    }
                    e.packet.write("c", slots.toList())
                }

                else -> error("Unsupported version.")
            }
        }
    }
}