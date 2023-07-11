package world.icebear03.splendidenchants.command

import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.expansion.createHelper
import world.icebear03.splendidenchants.api.EnchantAPI
import world.icebear03.splendidenchants.api.ItemAPI

@CommandHeader("splendidenchants", aliases = ["se", "spe", "nereusopus"])
object Commands {

    @CommandBody(permission = "splendidenchants.admin", aliases = ["help", "帮助"])
    val main = mainCommand {
        createHelper(true)
    }

    @CommandBody(permission = "splendidenchants.admin", aliases = ["et", "附魔"])
    val enchant = subCommand {
        execute<Player> { sender, _, _ ->

        }
    }

    @CommandBody(permission = "splendidenchants.admin", aliases = ["bk", "附魔书"])
    val book = subCommand {
        execute<Player> { sender, _, _ ->

        }
    }

    @CommandBody(permission = "splendidenchants.admin", aliases = ["rl", "重载"])
    val reload = subCommand {
        execute<Player> { sender, _, _ ->

        }
    }

    @CommandBody(permission = "splendidenchants.admin", aliases = ["模式"])
    val mode = subCommand {
        execute<Player> { sender, _, _ ->

        }
    }

    @CommandBody(permission = "splendidenchants.admin", aliases = ["ran", "rd", "随机附魔书"])
    val random = subCommand {
        execute<Player> { sender, _, _ ->

        }
    }

    @CommandBody(aliases = ["查询附魔"])
    val info = subCommand {
        execute<Player> { sender, _, _ ->

        }
    }

    @CommandBody(permission = "splendidenchants.admin", aliases = ["原版附魔兼容测试"])
    val test = subCommand {
        execute<Player> { sender, _, _ ->
            val item =
                ItemAPI.addEnchant(sender.inventory.itemInMainHand, EnchantAPI.getSplendidEnchant("测试附魔")!!, 2)
            ItemAPI.addEnchant(
                sender.inventory.itemInMainHand,
                EnchantAPI.getSplendidEnchant("测试附魔(复杂机制)")!!,
                2
            )
            sender.inventory.setItemInMainHand(item)
            sender.sendMessage("添加测试附魔成功")
        }
    }
}