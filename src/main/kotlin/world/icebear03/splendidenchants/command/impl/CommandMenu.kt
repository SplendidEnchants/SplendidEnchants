package world.icebear03.splendidenchants.command.impl

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.SimpleCommandBody
import taboolib.common.platform.command.subCommand
import taboolib.platform.util.onlinePlayers
import world.icebear03.splendidenchants.api.i18n.sendLang
import world.icebear03.splendidenchants.command.CommandExecutor
import world.icebear03.splendidenchants.command.CommandHandler
import world.icebear03.splendidenchants.ui.MainMenuUI

/**
 * SplendidEnchants
 * world.icebear03.splendidenchants.command.impl.CommandMenu
 *
 * @author mical
 * @since 2023/8/16 2:54 PM
 */
object CommandMenu : CommandExecutor {

    override val command: SimpleCommandBody
        get() = subCommand {
            dynamic("player", true) {
                suggestion<CommandSender> { _, _ -> onlinePlayers.map { it.name } }
                execute<CommandSender> { sender, args, _ ->
                    val player = args.getOrNull("player")?.let { Bukkit.getPlayer(it) } ?: (sender as? Player)
                    player?.let {
                        MainMenuUI.open(it)
                    } ?: sender.sendLang("command.subCommands.menu.fail")
                }
            }
        }

    override val name: String
        get() = "menu"

    init {
        CommandHandler.sub[CommandRandom.name] = this
    }
}