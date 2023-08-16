package world.icebear03.splendidenchants.enchant.mechanism.entry.operation

import org.bukkit.entity.LivingEntity

object Println {
    fun println(entity: LivingEntity, text: String) {
        println("§e来自玩家${entity.name}的附魔消息: §r$text")
        entity.sendMessage("§e来自玩家${entity.name}的附魔消息: §r$text")
    }
}