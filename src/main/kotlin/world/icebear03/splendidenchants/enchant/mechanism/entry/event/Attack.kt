package world.icebear03.splendidenchants.enchant.mechanism.entry.event

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.entity.EntityDamageByEntityEvent
import world.icebear03.splendidenchants.api.displayName
import world.icebear03.splendidenchants.api.replace
import world.icebear03.splendidenchants.api.subList
import world.icebear03.splendidenchants.enchant.mechanism.entry.`object`.ObjectLivingEntity
import world.icebear03.splendidenchants.enchant.mechanism.entry.`object`.ObjectPlayer

object Attack {

    fun modify(e: Event, entity: LivingEntity, params: List<String>, holders: MutableMap<String, Any>) {
        val event = e as EntityDamageByEntityEvent

        val damaged = event.entity as? LivingEntity ?: return
        Damaged.modify(event, damaged, params, holders)

        holders["攻击者"] = entity
        holders["攻击者名称"] = entity.displayName

        holders["蓄能程度"] = (entity as? Player)?.attackCooldown ?: 1.0f
        holders["是否为暴击"] = entity.fallDistance > 0
        holders["是否被格挡"] = (event.entity as? Player)?.isBlocking ?: false

        val variabled = params.map { it.replace(holders) }
        val type = variabled[0]
        val after = variabled.subList(1)

        when (type) {
            "攻击者" ->
                if (entity is Player) ObjectPlayer.modify(entity, after, holders)
                else ObjectLivingEntity.modify(entity, after, holders)

            else -> {}
        }
    }
}