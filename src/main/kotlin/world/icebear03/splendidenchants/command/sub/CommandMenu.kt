package world.icebear03.splendidenchants.command.sub

import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand
import world.icebear03.splendidenchants.ui.MainMenuUI


val commandMenu = subCommand {
    execute<Player> { sender, _, _ ->
        MainMenuUI.open(sender)
    }
}