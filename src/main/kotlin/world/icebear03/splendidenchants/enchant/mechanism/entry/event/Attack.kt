package world.icebear03.splendidenchants.enchant.mechanism.entry.event

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.entity.EntityDamageByEntityEvent
import taboolib.common.util.replaceWithOrder
import taboolib.module.kether.compileToJexl

object Attack {

    fun modifyEvent(e: Event, player: Player, line: String, replacerMap: ArrayList<Pair<String, String>>) {
        val event = e as EntityDamageByEntityEvent
        var param = line.replaceFirst(":", "::").split("::")[1]

        replacerMap.add(event.damage.toString() to "原伤害")
        //TODO 更多占位符...

        param = param.replaceWithOrder(*replacerMap.toArray())
        
        println(param)

        when (line.split(":")[0]) {
            "设置伤害" -> {
                event.damage = param.compileToJexl().eval() as Double
                player.sendMessage("加伤害，新伤害：${event.damage}")
            }

            else -> {}
        }
    }
}