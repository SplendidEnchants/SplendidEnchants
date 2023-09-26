package world.icebear03.splendidenchants.command.impl

import com.mcstarrysky.starrysky.command.CommandExecutor
import com.mcstarrysky.starrysky.i18n.sendLang
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.SimpleCommandBody
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.command.suggest
import taboolib.common.platform.command.suggestPlayers
import world.icebear03.splendidenchants.command.CommandHandler
import world.icebear03.splendidenchants.ui.AnvilUI
import world.icebear03.splendidenchants.ui.EnchantSearchUI
import world.icebear03.splendidenchants.ui.ItemCheckUI
import world.icebear03.splendidenchants.ui.MainMenuUI

object CommandMenu : CommandExecutor {

    val menus = listOf("main", "anvil", "search", "check")

    override val command: SimpleCommandBody
        get() = subCommand {
            execute<CommandSender> { sender, _, _ -> handle(sender) }
            dynamic("player", true) {
                suggestPlayers()
                execute<CommandSender> { sender, args, _ -> handle(sender, args["player"]) }

                dynamic(optional = true) {
                    suggest { menus }

                    execute<CommandSender> { sender, args, menu ->
                        handle(sender, menu, args["player"])
                    }
                }
            }
        }

    fun handle(sender: CommandSender, menu: String = "main", who: String? = null) {
        (who?.let { Bukkit.getPlayer(it) } ?: (sender as? Player))?.let { receiver ->
            when(menu) {
                "main" -> MainMenuUI.open(receiver)
                "anvil" -> AnvilUI.open(receiver)
                "search" -> EnchantSearchUI.open(receiver)
                "check" -> ItemCheckUI.open(receiver, mode = ItemCheckUI.CheckMode.FIND)
            }
        } ?: sender.sendLang("command.subCommands.menu.fail")
    }

    override val name: String
        get() = "menu"

    init {
        CommandHandler.sub[name] = this
    }
}