package world.icebear03.splendidenchants.command.sub

import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand
import taboolib.platform.util.giveItem
import world.icebear03.splendidenchants.api.EnchantAPI
import world.icebear03.splendidenchants.api.ItemAPI

val commandRandom = subCommand {
    execute<Player> { sender, _, _ ->
        val enchant = EnchantAPI.randomEnchant()
        sender.giveItem(ItemAPI.createBook(mapOf(enchant)))
        sender.sendMessage("§8[§6SplendidEnchants§8] §7已随机获得附魔 ${enchant.first.rarity.color}${enchant.first.basicData.name}§7, 等级 §f${enchant.second}§7.")
    }
}