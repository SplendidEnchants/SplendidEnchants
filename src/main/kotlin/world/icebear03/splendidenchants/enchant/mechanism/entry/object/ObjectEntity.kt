package world.icebear03.splendidenchants.enchant.mechanism.entry.`object`

import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.module.nms.getI18nName

object ObjectEntity {

    fun modifyEntity(
        entity: Entity,
        params: List<String>,
        holders: Map<String, String>
    ): Boolean {
        when (params[0]) {
            "" -> {}
            else -> return false
        }
        return true
    }

    fun toString(entity: LivingEntity): String {
        return (entity as? Player)?.name ?: entity.getI18nName()
    }
}