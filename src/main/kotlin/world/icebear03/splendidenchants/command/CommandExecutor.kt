package world.icebear03.splendidenchants.command

import taboolib.common.platform.command.SimpleCommandBody

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

    val description: String

    val usage: String
}