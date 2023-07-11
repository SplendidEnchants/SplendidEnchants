package world.icebear03.splendidenchants.command

import org.bukkit.Material
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.expansion.createHelper
import taboolib.module.kether.isInt
import world.icebear03.splendidenchants.api.EnchantAPI
import world.icebear03.splendidenchants.api.ItemAPI

@CommandHeader("splendidenchants", aliases = ["se", "spe", "nereusopus", "no"])
object Commands {

    @CommandBody(permission = "splendidenchants.admin", aliases = ["help", "帮助"])
    val main = mainCommand {
        createHelper(true)
    }

    @CommandBody(permission = "splendidenchants.admin", aliases = ["et", "附魔"])
    val enchant = subCommand {
        dynamic("enchant") {
            dynamic("level") {
                execute<Player> { sender, args, _ ->
                    val enchant = EnchantAPI.getSplendidEnchant(args["enchant"])
                    if (enchant == null) {
                        sender.sendMessage("附魔不存在")
                        return@execute
                    }
                    if (!args["level"].isInt()) {
                        sender.sendMessage("等级必须是整数")
                        return@execute
                    }
                    val level = args["level"].toInt()
                    if (level < 0) {
                        sender.sendMessage("等级必须是正整数")
                    }

                    val item = sender.inventory.itemInMainHand
                    if (item.type == Material.AIR) {
                        sender.sendMessage("手上必须有物品")
                        return@execute
                    }
                    if (level > 0) {
                        ItemAPI.addEnchant(item, enchant, level)
                        sender.sendMessage("附魔完成！")
                    }
                    if (level == 0) {
                        ItemAPI.removeEnchant(item, enchant)
                        sender.sendMessage("附魔已经清除！")
                    }
                }
            }
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
}