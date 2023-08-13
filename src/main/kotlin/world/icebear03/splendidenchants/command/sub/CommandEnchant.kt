package world.icebear03.splendidenchants.command.sub

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.subCommand
import taboolib.platform.util.isAir
import taboolib.platform.util.onlinePlayers
import world.icebear03.splendidenchants.api.addEt
import world.icebear03.splendidenchants.api.display
import world.icebear03.splendidenchants.api.removeEt
import world.icebear03.splendidenchants.api.splendidEt
import world.icebear03.splendidenchants.command.Commands

val commandEnchant = subCommand {
    dynamic("enchant") {
        suggestion<CommandSender> { _, _ -> Commands.enchantNamesAndIds }
        dynamic("level", true) {
            suggestion<Player> level@{ _, cmd ->
                val maxLevel = (splendidEt(cmd["enchant"])?.maxLevel ?: return@level listOf("附魔不存在"))
                buildList { repeat(maxLevel + 1) { add("$it") } }
            }
            dynamic("player", true) {
                suggestion<CommandSender> { _, _ -> onlinePlayers.map { it.name } }
                execute<CommandSender> { sender, args, _ ->
                    val enchant = splendidEt(args["enchant"])!!
                    val level = args.getOrNull("level")?.toInt() ?: enchant.maxLevel
                    val receiver = args.getOrNull("player")?.let { Bukkit.getPlayer(it) } ?: (sender as? Player)

                    receiver?.let {
                        val item = receiver.inventory.itemInMainHand
                        if (item.isAir) {
                            sender.sendMessage("§6SplendidEnchants §7>> 手持物品为空")
                            return@execute
                        }
                        val state = if (level == 0) "移除" else "添加"
                        if (level == 0) item.removeEt(enchant)
                        else item.addEt(enchant, level)
                        sender.sendMessage("§6SplendidEnchants §7>> 为§e${receiver.name}§7手上的物品${state}附魔${enchant.display(level)}")
                        receiver.sendMessage("§6SplendidEnchants §7>> 手上的物品被${state}添加了附魔${enchant.display(level)}")
                    } ?: sender.sendMessage("§6SplendidEnchants §7>> 控制台无法对物品进行附魔操作")
                }
            }
        }
    }
}