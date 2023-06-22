package me.icebear03.splendidenchants.api.nms

import org.bukkit.boss.BarColor
import org.bukkit.entity.Player
import taboolib.common.io.getClass
import taboolib.common.platform.function.submit
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.library.reflex.Reflex.Companion.setProperty
import taboolib.library.reflex.Reflex.Companion.unsafeInstance
import taboolib.module.chat.colored
import taboolib.module.nms.MinecraftVersion
import java.util.UUID

/**
 * SplendidEnchants
 * me.icebear03.splendidenchants.api.nms.NMSImpl
 *
 * @author mical
 * @since 2023/6/21 10:28 PM
 */
class NMSImpl : NMS() {

    override fun sendBossBar(player: Player, message: String, time: Int, overlay: String, color: BarColor) {
        val uuid = UUID.randomUUID()
        when (MinecraftVersion.major) {
            // 1.16
            8 -> {
                sendPacket(
                    player,
                    NMS16PacketPlayOutBoss(),
                    "a" to uuid,
                    "b" to NMS16PacketPlayOutBossAction.ADD,
                    "c" to CraftChatMessage16.fromString(message.colored()).first(),
                    "d" to 1f,
                    "e" to NMS16BossBattleBarColor.valueOf(color.name.uppercase()),
                    "f" to NMS16BossBattleBarStyle.valueOf(overlay),
                    "g" to false,
                    "h" to false,
                    "i" to false
                )
                submit(delay = time * 20L) {
                    sendPacket(
                        player,
                        NMS16PacketPlayOutBoss(),
                        "a" to uuid,
                        "b" to NMS16PacketPlayOutBossAction.REMOVE
                    )
                }
            }
            // 1.17, 1.18, 1.19, 1.20
            9, 10, 11, 12 -> {
                sendPacket(
                    player,
                    NMSPacketPlayOutBoss::class.java.unsafeInstance(),
                    "id" to uuid,
                    "operation" to getClass("net.minecraft.network.protocol.game.PacketPlayOutBoss\$a").unsafeInstance().also {
                        it.setProperty("name", CraftChatMessage19.fromString(message.colored()).first())
                        it.setProperty("progress", 1f)
                        it.setProperty("color", NMSBossBattleBarColor.valueOf(color.name.uppercase()))
                        it.setProperty("overlay", NMSBossBattleBarStyle.valueOf(overlay))
                        it.setProperty("darkenScreen", false)
                        it.setProperty("playMusic", false)
                        it.setProperty("createWorldFog", false)
                    }
                )
                submit(delay = time * 20L) {
                    sendPacket(
                        player,
                        NMSPacketPlayOutBoss::class.java.unsafeInstance(),
                        "id" to uuid,
                        "operation" to NMSPacketPlayOutBoss::class.java.getProperty<Any>("REMOVE_OPERATION", true)!!
                    )
                }
            }
        }
    }
}