package me.icebear03.splendidenchants.command

import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.expansion.createHelper

@CommandHeader("splendidenchant", aliases = ["se"])
object Commands {

    @CommandBody
    val main = mainCommand {
        createHelper(true)
    }
}