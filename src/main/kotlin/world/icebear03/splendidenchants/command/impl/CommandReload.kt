package world.icebear03.splendidenchants.command.impl

import com.mcstarrysky.starrysky.command.CommandExecutor
import com.mcstarrysky.starrysky.i18n.sendLang
import org.bukkit.command.CommandSender
import taboolib.common.platform.command.SimpleCommandBody
import taboolib.common.platform.command.subCommand
import world.icebear03.splendidenchants.SplendidEnchants
import world.icebear03.splendidenchants.command.CommandHandler

/**
 * SplendidEnchants
 * world.icebear03.splendidenchants.command.impl.CommandReload
 *
 * @author mical
 * @since 2023/8/16 3:02 PM
 */
object CommandReload : CommandExecutor {

    override val command: SimpleCommandBody
        get() = subCommand {
            execute<CommandSender> { sender, _, _ ->
                SplendidEnchants.reload()
                sender.sendLang("command.subCommands.reload.success")
            }
        }

    override val name: String
        get() = "reload"

    init {
        CommandHandler.sub[name] = this
    }
}