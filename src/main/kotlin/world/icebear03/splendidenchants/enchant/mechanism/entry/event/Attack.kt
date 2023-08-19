package world.icebear03.splendidenchants.enchant.mechanism.entry.event

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.entity.EntityDamageByEntityEvent
import taboolib.module.nms.getI18nName
import world.icebear03.splendidenchants.api.calcToDouble
import world.icebear03.splendidenchants.api.replace
import world.icebear03.splendidenchants.api.subList
import world.icebear03.splendidenchants.enchant.mechanism.entry.`object`.ObjectLivingEntity
import world.icebear03.splendidenchants.enchant.mechanism.entry.`object`.ObjectPlayer

object Attack {

    fun modify(e: Event, entity: LivingEntity, params: List<String>, holders: MutableMap<String, Any>) {
        val event = e as EntityDamageByEntityEvent

        holders["攻击者"] = entity
        holders["伤害者"] = entity
        holders["攻击者名称"] = entity.getI18nName()
        holders["被攻击者名称"] = event.entity.getI18nName()
        holders["伤害"] = event.damage
        holders["被攻击者"] = event.entity
        holders["被伤害者"] = event.entity
        holders["伤害类型"] = event.cause
        val variabled = params.map { it.replace(holders) }

        when (variabled[0]) {
            "设置伤害" -> event.damage = variabled[1].calcToDouble()
            "取消伤害", "取消" -> event.isCancelled = true
            "攻击者", "伤害者" ->
                if (entity is Player) ObjectPlayer.modify(entity, variabled.subList(1), holders)
                else ObjectLivingEntity.modify(entity, variabled.subList(1), holders)

            "被攻击者", "被伤害者" ->
                if (event.entity is Player) ObjectPlayer.modify(event.entity as Player, variabled.subList(1), holders)
                else ObjectLivingEntity.modify(event.entity as LivingEntity, variabled.subList(1), holders)

            else -> {}
        }
    }
}