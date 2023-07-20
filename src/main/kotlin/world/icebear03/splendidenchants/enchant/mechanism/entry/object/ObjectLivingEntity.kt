package world.icebear03.splendidenchants.enchant.mechanism.entry.`object`

import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import java.util.*

object ObjectLivingEntity {

    fun modifyLivingEntity(
        entity: LivingEntity,
        params: List<String>,
        replacerMap: ArrayList<Pair<String, String>>
    ): Boolean {
        if (ObjectEntity.modifyEntity(entity, params, replacerMap)) return true

        when (params[0]) {
            "" -> {}
            else -> return false
        }
        return true
    }

    fun toString(entity: LivingEntity): String {
        return "{LivingEntity=${entity.uniqueId}}"
    }

    fun fromString(string: String): LivingEntity? {
        val entity = Bukkit.getEntity(UUID.fromString(string))
        return if (entity is LivingEntity && !entity.isDead) entity
        else null
    }
}