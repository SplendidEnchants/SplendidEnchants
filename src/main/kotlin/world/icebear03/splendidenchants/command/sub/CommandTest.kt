package world.icebear03.splendidenchants.command.sub

import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand


val commandTest = subCommand {
    execute<Player> { sender, _, _ ->
        sender.giveExp(500)
    }
}