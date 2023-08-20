package world.icebear03.splendidenchants.enchant.mechanism.entry.`object`

import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.potion.PotionEffectType
import taboolib.platform.util.groundBlock
import world.icebear03.splendidenchants.api.*

object ObjectLivingEntity {

    fun modify(
        entity: LivingEntity,
        params: List<String>,
        holders: MutableMap<String, Any>
    ): Boolean {
        if (ObjectEntity.modify(entity, params, holders)) return true

        holders["血量"] = entity.health
        holders["最大血量"] = entity.maxHealth
        holders["脚下方块"] = entity.blockBelow ?: entity.groundBlock

        val variabled = params.map { it.replace(holders) }
        val type = variabled[0]
        val after = variabled.subList(1)

        when (type) {
            "施加药水效果" -> entity.effect(
                PotionEffectType.getByName(after[0])!!,
                after[1].calcToInt(),
                after[2].calcToInt()
            )

            "召唤雷电" -> {
                entity.world.strikeLightningEffect(entity.location)
                entity.realDamage((after.getOrElse(0) { "4.0" }).calcToDouble())
            }

            "伤害" -> {
                entity.damage(after[0].calcToDouble(), holders[after.getOrElse(1) { "null" }] as? Entity)
            }

            else -> return false
        }
        return true
    }
}