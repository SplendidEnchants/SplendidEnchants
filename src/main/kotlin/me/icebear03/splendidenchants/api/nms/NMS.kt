package me.icebear03.splendidenchants.api.nms

import me.icebear03.splendidenchants.`object`.Overlay
import org.bukkit.boss.BarColor
import org.bukkit.entity.Player
import taboolib.library.reflex.Reflex.Companion.setProperty
import taboolib.module.nms.nmsProxy
import taboolib.module.nms.sendPacket

/**
 * SplendidEnchants
 * me.icebear03.splendidenchants.api.nms.NMS
 *
 * @author mical
 * @since 2023/6/21 10:14 PM
 */
abstract class NMS {

    abstract fun sendBossBar(player: Player, message: String, progress: Float, time: Int, overlay: Overlay, color: BarColor)

    fun sendPacket(player: Player, packet: Any, vararg fields: Pair<Any, Any>) {
        fields.forEach { packet.setProperty(it.first.toString(), it.second) }
        player.sendPacket(packet)
    }

    companion object {

        val INSTANCE by lazy {
            nmsProxy<NMS>()
        }
    }
}