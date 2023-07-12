package world.icebear03.splendidenchants.command.sub

import taboolib.common.platform.command.SimpleCommandBody
import taboolib.common.platform.command.subCommand
import world.icebear03.splendidenchants.command.CommandExecutor
import world.icebear03.splendidenchants.command.CommandHandler

/**
 * SplendidEnchants
 * world.icebear03.splendidenchants.command.sub.CommandBook
 *
 * @author Mical
 * @since 2023/7/12 00:41
 */
object CommandBook : CommandExecutor {

    override val command: SimpleCommandBody = subCommand {

    }

    override val name: String
        get() = "book"

    override val description: String
        get() = "获取一本附魔书"

    override val usage: String
        get() = ""

    init {
        CommandHandler.sub[name] = this
    }
}