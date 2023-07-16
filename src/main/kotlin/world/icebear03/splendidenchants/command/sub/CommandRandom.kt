package world.icebear03.splendidenchants.command.sub

import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand
import taboolib.common.util.asList
import taboolib.platform.util.giveItem
import world.icebear03.splendidenchants.api.EnchantAPI
import world.icebear03.splendidenchants.api.ItemAPI
import world.icebear03.splendidenchants.enchant.data.Rarity

val commandRandom = subCommand {
    // 应加上新的可变参数-给予的玩家
    dynamic("rarity") {
        suggestionUncheck<Player> { _, _ ->
            Rarity.rarities.keys.toList().toMutableList().also {
                it.addAll(Rarity.rarities.values.asList())
            }
        }

        dynamic("level") {
            execute<Player> { sender, ctx, _ ->
                val enchant = EnchantAPI.randomEnchant(Rarity.fromIdOrName(ctx["rarity"]), ctx["level"].toInt())
                sender.giveItem(ItemAPI.createBook(mapOf(enchant)))
                sender.sendMessage("§8[§6SplendidEnchants§8] §7已随机获得附魔 ${enchant.first.rarity.color}${enchant.first.basicData.name}§7, 等级 §f${enchant.second}§7.")
            }
        }

        execute<Player> { sender, ctx, _ ->
            val enchant = EnchantAPI.randomEnchant(Rarity.fromIdOrName(ctx["rarity"]), null)
            sender.giveItem(ItemAPI.createBook(mapOf(enchant)))
            sender.sendMessage("§8[§6SplendidEnchants§8] §7已随机获得附魔 ${enchant.first.rarity.color}${enchant.first.basicData.name}§7, 等级 §f${enchant.second}§7.")
        }
    }
    execute<Player> { sender, _, _ ->
        val enchant = EnchantAPI.randomEnchant(null, null)
        sender.giveItem(ItemAPI.createBook(mapOf(enchant)))
        sender.sendMessage("§8[§6SplendidEnchants§8] §7已随机获得附魔 ${enchant.first.rarity.color}${enchant.first.basicData.name}§7, 等级 §f${enchant.second}§7.")
    }
}