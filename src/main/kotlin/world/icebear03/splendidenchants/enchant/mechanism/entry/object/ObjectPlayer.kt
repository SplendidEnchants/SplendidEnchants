package world.icebear03.splendidenchants.enchant.mechanism.entry.`object`

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import world.icebear03.splendidenchants.util.FurtherOperation

object ObjectPlayer {

    fun modifyPlayer(player: Player, params: List<String>, replacerMap: ArrayList<Pair<String, Any>>) {
//        player.sendMessage(params.toString())
        when (params[0]) {
            //测试方法，正规不会有
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
        }
    }
}