package world.icebear03.splendidenchants.api

import org.bukkit.entity.Player
import taboolib.common5.format
import java.util.*

object CooldownAPI {
    val stamps = mutableMapOf<UUID, MutableMap<String, Long>>()

    fun addStamp(player: Player, key: String) {
        val uuid = player.uniqueId
        if (!stamps.containsKey(uuid))
            stamps[uuid] = mutableMapOf()
        stamps[uuid]!![key] = System.currentTimeMillis()
    }

    fun clearStamp(player: Player, key: String? = null) {
        val uuid = player.uniqueId
        if (!stamps.containsKey(uuid))
            return
        if (key == null)
            stamps.remove(uuid)
        else
            stamps[uuid]!!.remove(key)
    }

    //pair#first 冷却是否结束，冷却中为false
    //pair#second 冷却若未结束，离结束还剩下的时间（秒）
    fun checkStamp(player: Player, key: String, cdInSec: Double, info: Boolean = false): Pair<Boolean, Double> {
        val uuid = player.uniqueId
        if (!stamps.containsKey(uuid))
            return true to 0.0
        if (!stamps[uuid]!!.containsKey(key))
            return true to 0.0
        val stamp = stamps[uuid]!![key]!!
        val period = (System.currentTimeMillis() - stamp) / 1000.0
        val left = (cdInSec - period).format(1)
        return if (period >= cdInSec)
            true to 0.0
        else {
            false to left
        }
    }
}