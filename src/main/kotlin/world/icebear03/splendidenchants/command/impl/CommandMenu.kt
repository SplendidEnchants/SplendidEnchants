package world.icebear03.splendidenchants.command.impl

import com.mcstarrysky.starrysky.command.CommandExecutor
import com.mcstarrysky.starrysky.i18n.sendLang
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.SimpleCommandBody
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.command.suggestPlayers
import world.icebear03.splendidenchants.command.CommandHandler
import world.icebear03.splendidenchants.ui.MainMenuUI

object CommandMenu : CommandExecutor {

    override val command: SimpleCommandBody
        get() = subCommand {
            execute<CommandSender> { sender, args, _ -> handle(sender, null) }
            dynamic("player", true) {
                suggestPlayers()
                execute<CommandSender> { sender, args, _ -> handle(sender, args["player"]) }
            }
        }

    fun handle(sender: CommandSender, who: String?) {
        (who?.let { Bukkit.getPlayer(it) } ?: (sender as? Player))?.let { receiver ->
            MainMenuUI.open(receiver)
        } ?: sender.sendLang("command.subCommands.menu.fail")
    }

    override val name: String
        get() = "menu"

    init {
        CommandHandler.sub[name] = this
    }
}