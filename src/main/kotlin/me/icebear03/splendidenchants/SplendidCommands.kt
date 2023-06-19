package me.icebear03.splendidenchants

import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.expansion.createHelper

/**
 * SplendidEnchants
 * me.icebear03.splendidenchants.SplendidCommands
 *
 * @author mical
 * @since 2023/6/19 7:49 PM
 */
@CommandHeader("splendidenchant", aliases = ["se"])
object SplendidCommands {

    @CommandBody
    val main = mainCommand {
        createHelper(true)
    }
}