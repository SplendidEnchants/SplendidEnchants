package world.icebear03.splendidenchants.command.sub

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand
import taboolib.platform.util.giveItem
import taboolib.platform.util.onlinePlayers
import world.icebear03.splendidenchants.api.book
import world.icebear03.splendidenchants.api.display
import world.icebear03.splendidenchants.api.splendidEt
import world.icebear03.splendidenchants.command.Commands

val commandBook = subCommand {
    dynamic("enchant") {
        suggestion<CommandSender> { _, _ -> Commands.enchantNamesAndIds }
        dynamic("level", true) {
            suggestion<Player> level@{ _, cmd ->
                val maxLevel = (splendidEt(cmd["enchant"])?.maxLevel ?: return@level listOf("附魔不存在"))
                buildList { repeat(maxLevel) { add("${it + 1}") } }
            }
            dynamic("player", true) {
                suggestion<CommandSender> { _, _ -> onlinePlayers.map { it.name } }
                execute<CommandSender> { sender, args, _ ->
                    val enchant = splendidEt(args["enchant"])!!
                    val level = args.getOrNull("level")?.toInt() ?: enchant.maxLevel
                    val receiver = args.getOrNull("player")?.let { Bukkit.getPlayer(it) } ?: (sender as? Player)
                    receiver?.let {
                        it.giveItem(enchant.book(level))
                        sender.sendMessage("§6SplendidEnchants §7>> 附魔书已经发送给§e${receiver.name}")
                        receiver.sendMessage("§6SplendidEnchants §7>> 你收到了附魔书${enchant.display(level)}")
                    } ?: sender.sendMessage("§6SplendidEnchants §7>> 控制台无法给予自己附魔书")
                }
            }
        }
    }
}