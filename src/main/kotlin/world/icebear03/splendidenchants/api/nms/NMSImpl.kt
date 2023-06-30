package world.icebear03.splendidenchants.api.nms

import org.bukkit.boss.BarColor
import org.bukkit.entity.Player
import taboolib.common.io.getClass
import taboolib.common.platform.function.submit
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.library.reflex.Reflex.Companion.setProperty
import taboolib.library.reflex.Reflex.Companion.unsafeInstance
import taboolib.module.chat.colored
import taboolib.module.nms.MinecraftVersion
import world.icebear03.splendidenchants.`object`.Overlay
import java.util.*

/**
 * SplendidEnchants
 * world.icebear03.splendidenchants.api.nms.NMSImpl
 *
 * @author mical
 * @since 2023/6/21 10:28 PM
 */
class NMSImpl : NMS() {

//    override fun toBukkitItemStack(item:ItemStack):org.bukkit.inventory.ItemStack{
//        //TODO
//    }

    override fun sendBossBar(
        player: Player,
        message: String,
        progress: Float,
        time: Int,
        overlay: Overlay,
        color: BarColor
    ) {
        val uuid = UUID.randomUUID()
        when (MinecraftVersion.major) {
            // 1.16, 其实 1.9-1.15 应该也可以用, 如果其他用户您有需要的话可以在您那里把这个 major 判断增加到 1.9。
            8 -> {
                sendPacket(
                    player,
                    NMS16PacketPlayOutBoss(),
                    "a" to uuid,
                    "b" to NMS16PacketPlayOutBossAction.ADD,
                    "c" to CraftChatMessage16.fromString(message.colored()).first(),
                    "d" to progress,
                    "e" to NMS16BossBattleBarColor.valueOf(color.name.uppercase()),
                    "f" to NMS16BossBattleBarStyle.valueOf(overlay.name.uppercase()),
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
                    "operation" to getClass("net.minecraft.network.protocol.game.PacketPlayOutBoss\$a").unsafeInstance()
                        .also {
                            it.setProperty("name", CraftChatMessage19.fromString(message.colored()).first())
                            it.setProperty("progress", progress)
                            it.setProperty("color", NMSBossBattleBarColor.valueOf(color.name.uppercase()))
                            it.setProperty("overlay", NMSBossBattleBarStyle.valueOf(overlay.name.uppercase()))
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