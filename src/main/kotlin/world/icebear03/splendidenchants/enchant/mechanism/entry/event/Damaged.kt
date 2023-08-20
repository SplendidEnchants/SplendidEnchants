package world.icebear03.splendidenchants.enchant.mechanism.entry.event

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.entity.EntityDamageEvent
import world.icebear03.splendidenchants.api.calcToDouble
import world.icebear03.splendidenchants.api.displayName
import world.icebear03.splendidenchants.api.replace
import world.icebear03.splendidenchants.api.subList
import world.icebear03.splendidenchants.enchant.mechanism.entry.`object`.ObjectLivingEntity
import world.icebear03.splendidenchants.enchant.mechanism.entry.`object`.ObjectPlayer

object Damaged {
    fun modify(e: Event, entity: LivingEntity, params: List<String>, holders: MutableMap<String, Any>) {
        val event = e as EntityDamageEvent

        holders["受伤者"] = entity
        holders["受伤者名称"] = entity.displayName

        holders["伤害类型"] = event.cause
        holders["伤害"] = event.damage

        val variabled = params.map { it.replace(holders) }
        val type = variabled[0]
        val after = variabled.subList(1)

        when (type) {
            "设置伤害" -> event.damage = after[0].calcToDouble()
            "取消伤害" -> event.isCancelled = true
            "受伤者" ->
                if (entity is Player) ObjectPlayer.modify(entity, after, holders)
                else ObjectLivingEntity.modify(entity, after, holders)

            else -> {}
        }
    }
}