package hamsteryds.splendidenchants.enchants

import org.bukkit.event.player.AsyncPlayerChatEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import taboolib.library.reflex.Reflex.Companion.invokeMethod
import taboolib.module.nms.PacketSendEvent
import taboolib.module.nms.nmsClass

/**
 * SplendidEnchants
 * hamsteryds.splendidenchants.enchants.PacketHandler
 *
 * @author mical
 * @since 2023/6/19 12:25 PM
 */
object PacketHandler {

    @SubscribeEvent
    fun onChat(e: AsyncPlayerChatEvent) {
        e.format = "%s 说: %s"
    }

    @SubscribeEvent
    fun onPacket(event: PacketSendEvent) {
        // println("你妈死了1")
        if (event.packet.name == "ClientboundServerDataPacket") {
            event.packet.write("enforcesSecureChat", true)
        //     println("你妈死了2")
        }
        if (event.packet.name == "ClientboundPlayerChatPacket") {
        //     println("你妈死了3")
            val d = event.packet.read<Any>("d")
            val e = event.packet.read<Any>("e")
            if (d != null) {
                val message = d.getProperty<Any>("a")

                // 如果聊天格式被修改，则此项不为空
                val changedFormat = e?.invokeMethod<Any>("getString")

                // 转为系统信息
                val newMessage = changedFormat ?: "${event.player.name} 说: $message"
                val chat = nmsClass("IChatBaseComponent").invokeMethod<Any>("literal", newMessage, isStatic = true)
                val packet = nmsClass("ClientboundSystemChatPacket").invokeConstructor(chat, false)
                event.packet.overwrite(packet)
            }
        }
    }
}