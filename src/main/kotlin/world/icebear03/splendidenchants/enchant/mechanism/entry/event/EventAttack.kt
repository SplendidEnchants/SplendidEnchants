package world.icebear03.splendidenchants.enchant.mechanism.entry.event

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import taboolib.platform.util.attacker
import world.icebear03.splendidenchants.enchant.mechanism.entry.internal.EventEntry
import world.icebear03.splendidenchants.enchant.mechanism.entry.internal.ObjectEntry
import world.icebear03.splendidenchants.enchant.mechanism.entry.internal.objLivingEntity
import world.icebear03.splendidenchants.enchant.mechanism.entry.internal.objString

object EventAttack : EventEntry<EntityDamageByEntityEvent>() {

    override fun modify(event: EntityDamageByEntityEvent, entity: LivingEntity, cmd: String, params: List<String>): Boolean {
        when (cmd) {
            else -> EventDamaged.modify(event, event.entity as LivingEntity, cmd, params)
        }
        return true
    }

    override fun get(event: EntityDamageByEntityEvent, objName: String): Pair<ObjectEntry<*>, Any?> {
        val attacker = event.attacker
        val damaged = event.entity
        return when (objName) {
            "攻击者" -> objLivingEntity.holderize(event.entity as LivingEntity)
            "蓄能程度" -> objString.holderize((attacker as? Player)?.attackCooldown ?: 1.0f)
            "是否为暴击" -> objString.holderize((attacker?.fallDistance ?: -1f) > 0)
            "是否被格挡" -> objString.holderize((damaged as? Player)?.isBlocking ?: false)
            else -> EventDamaged[event, objName]
        }
    }
}