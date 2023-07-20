package world.icebear03.splendidenchants.command.sub

import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand
import taboolib.module.kether.isInt
import taboolib.platform.util.isAir
import world.icebear03.splendidenchants.api.EnchantAPI
import world.icebear03.splendidenchants.api.ItemAPI
import world.icebear03.splendidenchants.command.Commands

val commandEnchant = subCommand {
    dynamic("enchant") {
        suggestionUncheck<Player> { _, _ -> Commands.enchantNamesAndIds }
        dynamic("level") {
            suggestionUncheck<Player> { _, _ -> listOf("等级(正整数)") }

            execute<Player> { sender, ctx, _ ->
                val enchant = EnchantAPI.getSplendidEnchant(ctx["enchant"])
                val level = ctx["level"]
                if (enchant == null) {
                    sender.sendMessage("§8[§6SplendidEnchants§8] §7附魔 §f${ctx["enchant"]} §7不存在.")
                    return@execute
                }
                if (!level.isInt()) {
                    sender.sendMessage("§8[§6SplendidEnchants§8] §7等级必须为数字.")
                    return@execute
                }
                if (level.toInt() < 0) {
                    sender.sendMessage("§8[§6SplendidEnchants§8] §7等级必须大于等于0(0为清除附魔).")
                    return@execute
                }
                val item = sender.inventory.itemInMainHand
                if (item.isAir) {
                    sender.sendMessage("§8[§6SplendidEnchants§8] §7你必须手持物品才可以对物品附魔.")
                    return@execute
                }

                when {
                    level.toInt() > 0 -> {
                        ItemAPI.addEnchant(item, enchant, level.toInt())
                        sender.sendMessage("§8[§6SplendidEnchants§8] §7已成功为物品添加附魔 ${enchant.rarity.color}${enchant.basicData.name}§7, 等级 §f$level§7.")
                    }

                    level.toInt() == 0 -> {
                        ItemAPI.removeEnchant(item, enchant)
                        sender.sendMessage("§8[§6SplendidEnchants§8] §7已成功为物品清除附魔 ${enchant.rarity.color}${enchant.basicData.name}§7.")
                    }
                }
            }
        }
    }
}