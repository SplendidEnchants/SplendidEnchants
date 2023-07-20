package world.icebear03.splendidenchants.enchant.mechanism.entry.event

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.entity.EntityDamageByEntityEvent
import taboolib.module.kether.compileToJexl
import world.icebear03.splendidenchants.enchant.mechanism.entry.`object`.ObjectPlayer
import world.icebear03.splendidenchants.util.FurtherOperation

object Attack {

    fun modifyEvent(e: Event, player: Player, params: List<String>, replacerMap: ArrayList<Pair<String, Any>>) {
        val event = e as EntityDamageByEntityEvent

        replacerMap.add(event.damage.toString() to "原伤害")
        //TODO 更多占位符...


        //param = param.replaceWithOrder(*replacerMap.toArray())

        when (params[0]) {
            "设置伤害" -> {
                event.damage = params[1].compileToJexl().eval() as Double
            }

            "攻击者", "伤害者" -> {
                ObjectPlayer.modifyPlayer(player, params.subList(1, params.size), replacerMap)
            }

            "攻击周围" -> {
                val range = params[1].toDouble()
                val damage = params[2].toDouble()
                player.getNearbyEntities(range, range, range).filterIsInstance<LivingEntity>().forEach {
                    if (it.uniqueId != player.uniqueId) {
                        FurtherOperation.furtherDamage(player, it, damage)
//                        player.sendMessage("${System.currentTimeMillis()}攻击生物造成伤害: $damage")
                    }
                }
            }

            else -> {}
        }
    }
}