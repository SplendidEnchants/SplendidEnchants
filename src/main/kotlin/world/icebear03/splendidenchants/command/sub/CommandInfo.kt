package world.icebear03.splendidenchants.command.sub

import taboolib.common.platform.command.SimpleCommandBody
import taboolib.common.platform.command.subCommand
import world.icebear03.splendidenchants.command.CommandExecutor
import world.icebear03.splendidenchants.command.CommandHandler

/**
 * SplendidEnchants
 * world.icebear03.splendidenchants.command.sub.CommandInfo
 *
 * @author Mical
 * @since 2023/7/12 00:42
 */
object CommandInfo : CommandExecutor {

    override val command: SimpleCommandBody = subCommand {

    }

    override val name: String
        get() = "info"

    override val description: String
        get() = "查询附魔详情信息"

    init {
        CommandHandler.sub[name] = this
    }
}