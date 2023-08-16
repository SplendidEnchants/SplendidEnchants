package world.icebear03.splendidenchants.enchant.mechanism.entry.`object`

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object ObjectPlayer {

    fun modifyPlayer(player: Player, params: List<String>, holders: Map<String, String>): Boolean {
        if (ObjectLivingEntity.modifyLivingEntity(player, params, holders)) return true

        when (params[0]) {
            "" -> {}
            else -> return false
        }
        return true
    }

    fun toString(entity: LivingEntity): String {
        return (entity as Player).name
    }
}