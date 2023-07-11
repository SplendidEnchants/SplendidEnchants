package world.icebear03.splendidenchants.command

import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.SimpleCommandBody
import taboolib.module.chat.colored
import taboolib.module.lang.asLangText

/**
 * SplendidEnchants
 * world.icebear03.splendidenchants.command.CommandExecutor
 *
 * @author Mical
 * @since 2023/7/12 00:19
 */
interface CommandExecutor {

    val command: SimpleCommandBody

    val name: String

    fun description(sender: ProxyCommandSender): String =
        sender.asLangText("COMMAND-${name.uppercase()}-DESCRIPTION").colored()
}