package world.icebear03.splendidenchants.enchant.mechanism.entry.`object`

import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.potion.PotionEffectType
import taboolib.common.platform.function.submit
import taboolib.platform.util.groundBlock
import world.icebear03.splendidenchants.api.*
import world.icebear03.splendidenchants.enchant.mechanism.entry.internal.ObjectEntry
import world.icebear03.splendidenchants.enchant.mechanism.entry.internal.objBlock
import world.icebear03.splendidenchants.enchant.mechanism.entry.internal.objEntity
import world.icebear03.splendidenchants.enchant.mechanism.entry.internal.objString
import java.util.*
import kotlin.math.pow

object ObjectLivingEntity : ObjectEntry<LivingEntity>() {

    override fun modify(
        obj: LivingEntity,
        cmd: String,
        params: List<String>
    ): Boolean {
        objEntity.modify(obj, cmd, params)
        when (cmd) {
            "施加药水效果" -> obj.effect(PotionEffectType.getByName(params[0])!!, params[1].calcToInt(), params[2].calcToInt())
            "霹雷" -> {
                obj.world.strikeLightningEffect(obj.location)
                obj.realDamage((params[0, "4.0"]).calcToDouble())
            }

            "伤害" -> obj.damage(params[0].calcToDouble(), objEntity.disholderize(params[1]))
            "弹飞" -> {
                val height = params[0].calcToDouble()
                val y = 0.1804 * height - 0.0044 * height.pow(2) + 0.00004 * height.pow(3)
                val vector = obj.velocity.also { it.y = 0.0; it.add(vector(0, y, 0)) }
                submit {
                    obj.velocity = vector
                }
            }
        }
        return true
    }

    override fun get(from: LivingEntity, objName: String): Pair<ObjectEntry<*>, Any?> {
        return when (objName) {
            "血量" -> objString.holderize(from.health)
            "最大血量" -> objString.holderize(from.maxHealth)
            "脚下方块" -> objBlock.holderize(from.blockBelow ?: from.groundBlock)
            else -> objEntity[from, objName]
        }
    }

    override fun holderize(obj: LivingEntity) = this to "生物=${obj.uniqueId}"

    override fun disholderize(holder: String) = Bukkit.getEntity(UUID.fromString(holder.replace("生物=", ""))) as? LivingEntity
}