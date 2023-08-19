package world.icebear03.splendidenchants.enchant.mechanism.entry.`object`

import org.bukkit.entity.LivingEntity
import org.bukkit.potion.PotionEffectType
import world.icebear03.splendidenchants.api.*

object ObjectLivingEntity {

    fun modify(
        entity: LivingEntity,
        params: List<String>,
        holders: MutableMap<String, Any>
    ): Boolean {
        if (ObjectEntity.modify(entity, params, holders)) return true

        holders["血量"] = entity.health
        holders["生命值"] = entity.health
        holders["最大血量"] = entity.maxHealth
        holders["最大生命值"] = entity.maxHealth

        val variabled = params.map { it.replace(holders) }

        when (variabled[0]) {
            "施加药水效果" -> entity.effect(
                PotionEffectType.getByName(variabled[1])!!,
                variabled[2].calcToInt(),
                variabled[3].calcToInt()
            )

            "召唤雷电" -> {
                entity.world.strikeLightningEffect(entity.location)
                entity.realDamage((variabled.getOrNull(1) ?: "4.0").calcToDouble())
            }

            else -> return false
        }
        return true
    }
}