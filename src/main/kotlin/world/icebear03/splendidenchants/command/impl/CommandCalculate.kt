package world.icebear03.splendidenchants.command.impl

import com.mcstarrysky.starrysky.command.CommandExecutor
import com.mcstarrysky.starrysky.i18n.sendLang
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.SimpleCommandBody
import taboolib.common.platform.command.subCommand
import taboolib.platform.util.onlinePlayers
import world.icebear03.splendidenchants.api.calculate
import world.icebear03.splendidenchants.command.CommandHandler
import world.icebear03.splendidenchants.ui.MainMenuUI
import java.lang.Exception

/**
 * SplendidEnchants
 * world.icebear03.splendidenchants.command.impl.CommandMenu
 *
 * @author mical
 * @since 2023/8/16 2:54 PM
 */
object CommandCalculate : CommandExecutor {

    override val command: SimpleCommandBody
        get() = subCommand {
            dynamic("expression", true) {
                execute<CommandSender> { sender, args, _ ->
                    sender.sendMessage(run {
                        try {
                            args["expression"].calculate()
                        } catch (e: Exception) {
                            "error"
                        }
                    })
                }
            }
        }

    override val name: String
        get() = "calculate"

    init {
        CommandHandler.sub[name] = this
    }
}