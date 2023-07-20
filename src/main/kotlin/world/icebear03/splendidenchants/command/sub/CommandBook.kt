package world.icebear03.splendidenchants.command.sub

import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand
import taboolib.module.kether.isInt
import world.icebear03.splendidenchants.api.EnchantAPI
import world.icebear03.splendidenchants.api.ItemAPI
import world.icebear03.splendidenchants.command.Commands

val commandBook = subCommand {
    // 应加上新的可变参数-给予的玩家
    dynamic("enchant") {
        suggestionUncheck<Player> { _, _ -> Commands.enchantNamesAndIds }
        dynamic("level") {
            suggestionUncheck<Player> { _, _ -> listOf("等级(正整数)") }

            execute<Player> { sender, ctx, _ ->
                val enchant = EnchantAPI.getSplendidEnchant(ctx["enchant"])

                if (enchant == null) {
                    sender.sendMessage("§6SplendidEnchants §7>> §7附魔 §f${ctx["enchant"]} §7不存在.")
                    return@execute
                }
                val level = ctx["level"]
                if (!level.isInt()) {
                    sender.sendMessage("§6SplendidEnchants §7>> §7等级必须为数字.")
                    return@execute
                }
                if (level.toInt() <= 0) {
                    sender.sendMessage("§6SplendidEnchants §7>> §7等级必须大于0.")
                    return@execute
                }
                ItemAPI.giveBook(sender, enchant, level.toInt())
                sender.sendMessage("§6SplendidEnchants §7>> §7已成功给予玩家 §e${sender.name} §7附魔书 ${enchant.rarity.color}${enchant.basicData.name}§7, 等级 §f$level§7.")
            }
        }
        execute<Player> { sender, ctx, _ ->
            val enchant = EnchantAPI.getSplendidEnchant(ctx["enchant"])

            if (enchant == null) {
                sender.sendMessage("§6SplendidEnchants §7>> §7附魔 §f${ctx["enchant"]} §7不存在.")
                return@execute
            }

            ItemAPI.giveBook(sender, enchant)
            sender.sendMessage("§6SplendidEnchants §7>> §7已成功给予玩家 §e${sender.name} §7附魔书 ${enchant.rarity.color}${enchant.basicData.name}§7, 等级 §f${enchant.maxLevel}§7.")
        }
    }
}