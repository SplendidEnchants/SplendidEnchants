package me.icebear03.splendidenchants.api.nms

import org.bukkit.boss.BarColor
import org.bukkit.entity.Player
import taboolib.common.io.getClass
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
            }
        }
    }
}

// FIXME: 下面的东西别动，谁动谁孙子
/*
package me.icebear03.splendidenchants.api.nms

import com.google.gson.JsonParser
import com.google.gson.stream.JsonReader
import me.icebear03.splendidenchants.api.nms.dataserializer.createDataSerializer
import me.icebear03.splendidenchants.gson
import org.bukkit.boss.BarColor
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.sendPacket
import java.util.*

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
                player.sendPacket(
                    NMS16PacketPlayOutBoss().also {
                        it.a(
                            createDataSerializer {
                                writeUUID(uuid)
                                writeVarInt(
                                    NMS16PacketPlayOutBossAction.values().indexOf(NMS16PacketPlayOutBossAction.ADD)
                                )
                                writeNullable(message) {
                                    val component = craftChatMessageFromString(it)
                                    val json = craftChatSerializerToJson(component)
                                    writeUtf(json, 262144)
                                }
                                writeFloat(1f)
                                writeVarInt(
                                    NMS16BossBattleBarColor.values()
                                        .indexOf(NMS16BossBattleBarColor.valueOf(color.name.uppercase()))
                                )
                                writeVarInt(
                                    NMS16BossBattleBarStyle.values().indexOf(NMS16BossBattleBarStyle.valueOf(overlay))
                                )
                                writeBoolean(false)
                                writeBoolean(false)
                                writeBoolean(false)
                            }.toNMS() as NMS16PacketDataSerializer
                        )
                    }
                )
                submit(delay = time * 1000L) {
                    player.sendPacket(
                        NMS16PacketPlayOutBoss().also {
                            it.a(
                                createDataSerializer {
                                    writeUUID(uuid)
                                    writeVarInt(
                                        NMS16PacketPlayOutBossAction.values()
                                            .indexOf(NMS16PacketPlayOutBossAction.REMOVE)
                                    )
                                }.toNMS() as NMS16PacketDataSerializer
                            )
                        }
                    )
                }
            }
            // 1.17, 1.18, 1.19, 1.20
            9, 10, 11, 12 -> {
                NMSPacketPlayOutBoss(createDataSerializer {
                    writeUUID(uuid)
                    writeVarInt(0) // ADD
                    writeNullable(message) {
                        val component = craftChatMessageFromString(it)
                        val json = craftChatSerializerToJson(component)
                        writeUtf(json, 262144)
                    }
                    writeFloat(1f)
                    writeVarInt(
                        NMSBossBattleBarColor.values().indexOf(NMSBossBattleBarColor.valueOf(color.name.uppercase()))
                    )
                    writeVarInt(NMSBossBattleBarStyle.values().indexOf(NMSBossBattleBarStyle.valueOf(overlay)))
                    writeByte(nmsUniversalPacketPlayOutBossEncodeProperties(false, false, false).toByte())
                }.toNMS() as NMSPacketDataSerializer)
                submit(delay = time * 1000L) {
                    player.sendPacket(
                        NMSPacketPlayOutBoss(createDataSerializer {
                            writeUUID(uuid)
                            writeVarInt(1) // REMOVE
                        }.toNMS() as NMSPacketDataSerializer)
                    )
                }
            }
        }
    }

    private fun nmsUniversalPacketPlayOutBossEncodeProperties(var0: Boolean, var1: Boolean, var2: Boolean): Int {
        var var3 = 0
        if (var0) {
            var3 = var3 or 1
        }

        if (var1) {
            var3 = var3 or 2
        }

        if (var2) {
            var3 = var3 or 4
        }

        return var3
    }

    private fun craftChatMessageFromString(message: String): Any {
        return CraftChatMessage19.fromString(message)[0]
    }

    private fun craftChatSerializerToJson(compound: Any): String {
        return formatJson(
            if (MinecraftVersion.isUniversal) {
                NMSChatSerializer.toJson(compound as NMSIChatBaseComponent)
            } else {
                NMS16ChatSerializer.a(compound as NMS16IChatBaseComponent)
            }
        )
    }

    private fun formatJson(json: String): String {
        return gson.toJson(JsonParser().parse(json))
    }
}
*/