package world.icebear03.splendidenchants.command.sub

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand
import taboolib.platform.util.onlinePlayers
import world.icebear03.splendidenchants.ui.MainMenuUI


val commandMenu = subCommand {
    dynamic("player", true) {
        suggestion<CommandSender> { _, _ -> onlinePlayers.map { it.name } }
        execute<CommandSender> { sender, args, _ ->
            val player = args.getOrNull("player")?.let { Bukkit.getPlayer(it) } ?: (sender as? Player)
            player?.let {
                MainMenuUI.open(it)
            } ?: sender.sendMessage("§6SplendidEnchants §7>> 控制台无法打开菜单")
        }
    }
}