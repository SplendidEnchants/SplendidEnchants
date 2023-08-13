package world.icebear03.splendidenchants.command.sub

import org.bukkit.command.CommandSender
import taboolib.common.platform.command.subCommand
import world.icebear03.splendidenchants.SplendidEnchants

val commandReload = subCommand {
    execute<CommandSender> { sender, _, _ ->
        SplendidEnchants.reload()
        sender.sendMessage("§6SplendidEnchants §7>> §a插件已经重载完毕")
    }
}