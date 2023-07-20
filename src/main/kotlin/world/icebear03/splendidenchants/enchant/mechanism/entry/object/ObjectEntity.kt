package world.icebear03.splendidenchants.enchant.mechanism.entry.`object`

import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import java.util.*

object ObjectEntity {

    fun modifyEntity(
        entity: Entity,
        params: List<String>,
        replacerMap: ArrayList<Pair<String, String>>
    ): Boolean {
        when (params[0]) {
            "" -> {}
            else -> return false
        }
        return true
    }

    fun toString(entity: Entity): String {
        return "{Entity=${entity.uniqueId}}"
    }

    fun fromString(string: String): Entity? {
        val entity = Bukkit.getEntity(UUID.fromString(string)) ?: return null
        return if (!entity.isDead) entity
        else null
    }
}