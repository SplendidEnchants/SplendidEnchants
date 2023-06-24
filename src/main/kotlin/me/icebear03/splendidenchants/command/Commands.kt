package me.icebear03.splendidenchants.command

import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.expansion.createHelper

@CommandHeader("splendidenchant", aliases = ["se"])
object Commands {

    @CommandBody
    val main = mainCommand {
        createHelper(true)
    }

    @CommandBody
    val testBossBar = subCommand {
        execute<Player> { sender, _, _ ->
//            NMS.INSTANCE.sendBossBar(sender, "你妈死了", 10f, "PROGRESS", BarColor.BLUE)
        }
    }
}