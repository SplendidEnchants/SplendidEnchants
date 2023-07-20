package world.icebear03.splendidenchants.enchant.mechanism.entry.event

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.entity.EntityDamageByEntityEvent
import taboolib.module.kether.compileToJexl
import world.icebear03.splendidenchants.enchant.mechanism.entry.`object`.ObjectLivingEntity
import world.icebear03.splendidenchants.enchant.mechanism.entry.`object`.ObjectPlayer
import world.icebear03.splendidenchants.util.StringUtils

object Attack {

    fun modifyEvent(e: Event, player: Player, params: List<String>, replacerMap: ArrayList<Pair<String, String>>) {
        val event = e as EntityDamageByEntityEvent

        replacerMap.add(event.damage.toString() to "伤害")
        replacerMap.add(ObjectPlayer.toString(player) to "攻击者")
        replacerMap.add(ObjectPlayer.toString(player) to "伤害者")
        replacerMap.add(ObjectLivingEntity.toString(event.entity as LivingEntity) to "被攻击者")
        replacerMap.add(ObjectLivingEntity.toString(event.entity as LivingEntity) to "被伤害者")
        replacerMap.add(event.cause.toString() to "伤害类型")

        val replaced = StringUtils.replaceParams(params, replacerMap)
        println(replaced)

        when (replaced.first) {
            "设置伤害" -> {
                event.damage = replaced.second[0].compileToJexl().eval() as Double
            }

            "取消伤害", "取消" -> {
                event.isCancelled = true
            }

            "攻击者", "伤害者" -> {
                ObjectPlayer.modifyPlayer(player, replaced.second, replacerMap)
            }

            "被攻击者", "被伤害者" -> {
                if (event.entity is Player)
                    ObjectPlayer.modifyPlayer(event.entity as Player, replaced.second, replacerMap)
                else {
                    ObjectLivingEntity.modifyLivingEntity(event.entity as LivingEntity, replaced.second, replacerMap)
                }
            }

            else -> {}
        }
    }
}