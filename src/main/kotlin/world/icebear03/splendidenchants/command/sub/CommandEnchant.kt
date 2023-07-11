package world.icebear03.splendidenchants.command.sub

import taboolib.common.platform.command.SimpleCommandBody
import taboolib.common.platform.command.subCommand
import world.icebear03.splendidenchants.command.CommandExecutor
import world.icebear03.splendidenchants.command.CommandHandler

/**
 * SplendidEnchants
 * world.icebear03.splendidenchants.command.sub.CommandEnchant
 *
 * @author Mical
 * @since 2023/7/12 00:41
 */
object CommandEnchant : CommandExecutor {

    override val command: SimpleCommandBody = subCommand {

    }

    override val name: String = "enchant"

    init {
        CommandHandler.sub[name] = this
    }
}