package world.icebear03.splendidenchants.enchant.mechanism.entry.event

import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDamageEvent
import world.icebear03.splendidenchants.api.calcToDouble
import world.icebear03.splendidenchants.enchant.mechanism.entry.internal.EventEntry
import world.icebear03.splendidenchants.enchant.mechanism.entry.internal.ObjectEntry
import world.icebear03.splendidenchants.enchant.mechanism.entry.internal.objLivingEntity
import world.icebear03.splendidenchants.enchant.mechanism.entry.internal.objString

object EventDamaged : EventEntry<EntityDamageEvent>() {

    override fun modify(event: EntityDamageEvent, entity: LivingEntity, cmd: String, params: List<String>): Boolean {
        when (cmd) {
            "设置伤害" -> {
                val dmg = params[0].calcToDouble()
                if (dmg < 0.0) event.isCancelled = true
                else event.damage = dmg
            }

            "取消伤害" -> event.isCancelled = true
        }
        return true
    }

    override fun get(event: EntityDamageEvent, objName: String): Pair<ObjectEntry<*>, Any?> {
        return when (objName) {
            "受伤者" -> objLivingEntity.holderize(event.entity as LivingEntity)
            "伤害类型" -> objString.h(event.cause.toString())
            "伤害" -> objString.h(event.damage)
            else -> objString.h(null)
        }
    }
}