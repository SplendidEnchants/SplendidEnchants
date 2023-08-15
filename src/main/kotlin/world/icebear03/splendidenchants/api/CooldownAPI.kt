package world.icebear03.splendidenchants.api

import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common5.format
import java.util.*

val stamps = mutableMapOf<UUID, MutableMap<String, Long>>()

fun Player.addCd(key: String) {
    stamps[uniqueId]!![key] = System.currentTimeMillis()
}

fun Player.removeCd(key: String) {
    stamps[uniqueId]!!.remove(key)
}

fun Player.clearCd() {
    stamps[uniqueId]!!.clear()
}

//pair#first 冷却是否结束，冷却中为false
//pair#second 冷却若未结束，离结束还剩下的时间（秒）
fun Player.checkCd(key: String, cd: Double, info: Boolean = false): Pair<Boolean, Double> {
    if (!stamps[uniqueId]!!.containsKey(key))
        return true to 0.0
    val tmp = (cd - (System.currentTimeMillis() - stamps[uniqueId]!![key]!!) / 1000.0).format(1)
    return if (tmp <= 0.0) {
        sendMessage("冷却中，还需${tmp}秒")
        true to -1.0
    } else false to maxOf(tmp, 0.0)
}

object CooldownData {
    @SubscribeEvent
    fun join(event: PlayerJoinEvent) {
        stamps[event.player.uniqueId] = mutableMapOf()
    }
}