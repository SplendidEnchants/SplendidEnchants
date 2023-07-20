package world.icebear03.splendidenchants.enchant.mechanism.entry.operation

import org.bukkit.entity.Player

object Println {
    fun println(player: Player, text: String) {
        println("§e来自玩家${player.name}的附魔消息: §r$text")
    }
}