package world.icebear03.splendidenchants.command

import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.expansion.createHelper
import world.icebear03.splendidenchants.api.EnchantAPI
import world.icebear03.splendidenchants.api.ItemAPI
import world.icebear03.splendidenchants.enchant.EnchantDisplayer

@CommandHeader("splendidenchants", aliases = ["se", "附魔", "spe"])
object Commands {

    @CommandBody
    val main = mainCommand {
        createHelper(true)
    }

    @CommandBody(permission = "splendidenchants.admin", aliases = ["展示"])
    val displayEnchants = subCommand {
        execute<Player> { sender, _, _ ->
            val item = EnchantDisplayer.display(sender.inventory.itemInMainHand, sender)
            sender.inventory.setItemInMainHand(item)
            sender.sendMessage("展示成功")
        }
    }

    @CommandBody(permission = "splendidenchants.admin", aliases = ["原版附魔兼容测试"])
    val testvanilla = subCommand {
        execute<Player> { sender, _, _ ->
            val item = ItemAPI.addEnchant(sender.inventory.itemInMainHand, EnchantAPI.getSplendidEnchant("锋利")!!, 3)
            sender.inventory.setItemInMainHand(item)
            sender.sendMessage("测试成功")
        }
    }
}