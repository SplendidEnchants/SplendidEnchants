package world.icebear03.splendidenchants.command.impl

import com.mcstarrysky.starrysky.command.CommandExecutor
import com.mcstarrysky.starrysky.i18n.sendLang
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.SimpleCommandBody
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.command.suggestPlayers
import taboolib.module.kether.isInt
import taboolib.platform.util.giveItem
import world.icebear03.splendidenchants.api.book
import world.icebear03.splendidenchants.api.display
import world.icebear03.splendidenchants.api.drawEt
import world.icebear03.splendidenchants.command.CommandHandler
import world.icebear03.splendidenchants.enchant.data.Rarity
import world.icebear03.splendidenchants.enchant.data.rarities
import world.icebear03.splendidenchants.enchant.data.rarity

object CommandRandom : CommandExecutor {

    override val command: SimpleCommandBody
        get() = subCommand {
            dynamic("rarity") {
                suggestion<CommandSender> { _, _ -> rarities.map { it.key } + rarities.map { it.value.name } }
                execute<CommandSender> { sender, args, _ -> handle(sender, null, rarity(args["rarity"])!!) }
                dynamic("level", true) {
                    suggestionUncheck<CommandSender> { _, _ -> listOf("等级") }
                    execute<CommandSender> { sender, args, _ -> handle(sender, null, rarity(args["rarity"])!!, args["level"]) }
                    dynamic("player", true) {
                        suggestPlayers()
                        execute<CommandSender> { sender, args, _ -> handle(sender, args["player"], rarity(args["rarity"])!!, args["level"]) }
                    }
                }
            }
        }

    fun handle(sender: CommandSender, who: String?, rarity: Rarity, level: String? = "100") {
        val enchant = rarity.drawEt() ?: run {
            sender.sendLang("command.subCommands.random.rarity")
            return
        }
        if (level?.isInt() != true) {
            sender.sendLang("command.subCommands.random.number")
            return
        }
        val lv = level.toInt().coerceAtLeast(1).coerceAtMost(enchant.maxLevel)
        (who?.let { Bukkit.getPlayer(it) } ?: (sender as? Player))?.let { receiver ->
            receiver.giveItem(enchant.book(lv))
            sender.sendLang("command.subCommands.random.sender", "name" to receiver.name, "enchantment" to enchant.display(lv))
            receiver.sendLang("command.subCommands.random.receiver", "enchantment" to enchant.display(lv))
        } ?: sender.sendLang("command.subCommands.random.fail")
    }

    override val name: String
        get() = "random"

    init {
        CommandHandler.sub[name] = this
    }
}