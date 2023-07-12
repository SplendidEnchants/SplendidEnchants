package world.icebear03.splendidenchants.enchant.mechanism.entry.event

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.entity.EntityDamageByEntityEvent
import taboolib.module.kether.compileToJexl
import world.icebear03.splendidenchants.enchant.mechanism.entry.`object`.ObjectPlayer

object Attack {

    fun modifyEvent(e: Event, player: Player, params: List<String>, replacerMap: ArrayList<Pair<String, String>>) {
        val event = e as EntityDamageByEntityEvent

        replacerMap.add(event.damage.toString() to "原伤害")
        //TODO 更多占位符...


        //param = param.replaceWithOrder(*replacerMap.toArray())

        when (params[0]) {
            "设置伤害" -> {
                event.damage = params[1].compileToJexl().eval() as Double
            }

            "玩家", "攻击者" -> {
                ObjectPlayer.modifyPlayer(player, params.subList(1, params.size), replacerMap)
            }

            else -> {}
        }
    }
}