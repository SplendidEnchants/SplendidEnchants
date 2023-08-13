package world.icebear03.splendidenchants.command.sub

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand
import taboolib.module.kether.isInt
import taboolib.platform.util.giveItem
import taboolib.platform.util.onlinePlayers
import world.icebear03.splendidenchants.api.book
import world.icebear03.splendidenchants.api.display
import world.icebear03.splendidenchants.api.drawEt
import world.icebear03.splendidenchants.enchant.data.rarities
import world.icebear03.splendidenchants.enchant.data.rarity

val commandRandom = subCommand {
    dynamic("rarity") {
        suggestion<CommandSender> { _, _ -> rarities.map { it.key } + rarities.map { it.value.name } }
        dynamic("level", true) {
            suggestionUncheck<CommandSender> { _, _ -> listOf("等级") }
            dynamic("player", true) {
                suggestion<CommandSender> { _, _ -> onlinePlayers.map { it.name } }
                execute<CommandSender> { sender, args, _ ->
                    val rarity = rarity(args["rarity"])!!
                    val enchant = rarity.drawEt() ?: run {
                        sender.sendMessage("§6SplendidEnchants §7>> 品质中附魔为空")
                        return@execute
                    }
                    if (args.getOrNull("level")?.isInt() != true) {
                        sender.sendMessage("§6SplendidEnchants §7>> 请输入正整数")
                        return@execute
                    }
                    val level = args["level"].toInt().coerceAtLeast(1).coerceAtMost(enchant.maxLevel)
                    val receiver = args.getOrNull("player")?.let { Bukkit.getPlayer(it) } ?: (sender as? Player)
                    receiver?.let {
                        it.giveItem(enchant.book(level))
                        sender.sendMessage("§6SplendidEnchants §7>> 给§e${receiver.name}§7发送了随机的附魔书${enchant.display(level)}")
                        receiver.sendMessage("§6SplendidEnchants §7>> 你收到了随机附魔书${enchant.display(level)}")
                    } ?: sender.sendMessage("§6SplendidEnchants §7>> 控制台无法接受随机附魔书")
                }
            }
        }
    }
}