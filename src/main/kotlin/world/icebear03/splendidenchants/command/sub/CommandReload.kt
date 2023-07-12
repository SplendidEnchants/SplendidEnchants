package world.icebear03.splendidenchants.command.sub

import taboolib.common.platform.command.SimpleCommandBody
import taboolib.common.platform.command.subCommand
import world.icebear03.splendidenchants.command.CommandExecutor
import world.icebear03.splendidenchants.command.CommandHandler

/**
 * SplendidEnchants
 * world.icebear03.splendidenchants.command.sub.CommandReload
 *
 * @author Mical
 * @since 2023/7/12 00:41
 */
object CommandReload : CommandExecutor {

    override val command: SimpleCommandBody = subCommand {

    }

    override val name: String
        get() = "reload"

    override val description: String
        get() = "重新加载插件"

    init {
        CommandHandler.sub[name] = this
    }
}