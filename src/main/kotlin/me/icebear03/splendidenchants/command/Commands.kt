package me.icebear03.splendidenchants.command

import me.icebear03.splendidenchants.enchant.EnchantDisplayer
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.expansion.createHelper

@CommandHeader("splendidenchants", aliases = ["se", "附魔", "spe"])
object Commands {

    @CommandBody
    val main = mainCommand {
        createHelper(true)
    }

    @CommandBody(permission = "splendidenchants.admin", aliases = ["展示"])
    val displayEnchants = subCommand {
        execute<Player> { sender, context, argument ->
            run {
                val item = EnchantDisplayer.adaptItem(sender.inventory.itemInMainHand, sender)
                sender.inventory.setItemInMainHand(item)
                sender.sendMessage("展示成功")
            }
        }
    }
}