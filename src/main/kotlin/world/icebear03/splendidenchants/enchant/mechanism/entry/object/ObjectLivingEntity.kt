package world.icebear03.splendidenchants.enchant.mechanism.entry.`object`

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import taboolib.module.nms.getI18nName

object ObjectLivingEntity {

    fun modifyLivingEntity(
        entity: LivingEntity,
        params: List<String>,
        holders: Map<String, String>
    ): Boolean {
        if (ObjectEntity.modifyEntity(entity, params, holders)) return true

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