package world.icebear03.splendidenchants.command.sub

import org.bukkit.Material
import org.bukkit.entity.Player
import taboolib.common.platform.command.SimpleCommandBody
import taboolib.common.platform.command.subCommand
import taboolib.module.kether.isInt
import world.icebear03.splendidenchants.api.EnchantAPI
import world.icebear03.splendidenchants.api.ItemAPI
import world.icebear03.splendidenchants.command.CommandExecutor
import world.icebear03.splendidenchants.command.CommandHandler

/**
 * SplendidEnchants
 * world.icebear03.splendidenchants.command.sub.CommandEnchant
 *
 * @author Mical
 * @since 2023/7/12 00:41
 */
object CommandEnchant : CommandExecutor {

    override val command: SimpleCommandBody = subCommand {
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

    override val name: String
        get() = "enchant"

    override val description: String
        get() = "为手中物品附魔"

    init {
        CommandHandler.sub[name] = this
    }
}