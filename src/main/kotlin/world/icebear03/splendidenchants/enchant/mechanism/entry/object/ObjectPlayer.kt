package world.icebear03.splendidenchants.enchant.mechanism.entry.`object`

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

object ObjectPlayer {

    fun modifyPlayer(player: Player, params: List<String>, replacerMap: ArrayList<Pair<String, String>>): Boolean {
        if (ObjectLivingEntity.modifyLivingEntity(player, params, replacerMap)) return true

        when (params[0]) {
            "" -> {}
            else -> return false
        }
        return true
    }

    fun toString(player: Player): String {
        return "{Player=${player.uniqueId}}"
    }

    fun fromString(string: String): Player? {
        val uuid = string.replace("{", "").replace("}", "").replace("Player:", "")
        val offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid))
        return if (offlinePlayer.isOnline) offlinePlayer as Player
        else null
    }
}