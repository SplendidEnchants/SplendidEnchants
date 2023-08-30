package world.icebear03.splendidenchants.command.impl

import com.mcstarrysky.starrysky.command.CommandExecutor
import com.mcstarrysky.starrysky.i18n.sendLang
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.SimpleCommandBody
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.command.suggestPlayers
import taboolib.platform.util.giveItem
import world.icebear03.splendidenchants.api.book
import world.icebear03.splendidenchants.api.display
import world.icebear03.splendidenchants.api.splendidEt
import world.icebear03.splendidenchants.command.CommandHandler
import world.icebear03.splendidenchants.enchant.SplendidEnchant

object CommandBook : CommandExecutor {

    override val command: SimpleCommandBody
        get() = subCommand cmd@{
            dynamic("enchant") {
                suggestion<CommandSender> { _, _ -> CommandHandler.enchantNamesAndIds }
                execute<CommandSender> { sender, args, _ -> handle(sender, null, splendidEt(args["enchant"])!!) }
                dynamic("level", true) {
                    suggestion<Player> level@{ _, cmd ->
                        val maxLevel = (splendidEt(cmd["enchant"])?.maxLevel ?: return@level listOf())
                        buildList { repeat(maxLevel) { add("${it + 1}") } }
                    }
                    execute<CommandSender> { sender, args, _ -> handle(sender, null, splendidEt(args["enchant"])!!, args["level"].toInt()) }
                    dynamic("player", true) {
                        suggestPlayers()
                        execute<CommandSender> { sender, args, _ -> handle(sender, args["player"], splendidEt(args["enchant"])!!, args["level"].toInt()) }
                    }
                }
            }
        }

    fun handle(sender: CommandSender, who: String?, enchant: SplendidEnchant, level: Int = enchant.maxLevel) {
        (who?.let { Bukkit.getPlayer(it) } ?: (sender as? Player))?.let { receiver ->
            receiver.giveItem(enchant.book(level))
            sender.sendLang("command.subCommands.book.sender", "name" to receiver.name)
            receiver.sendLang("command.subCommands.book.receiver", "enchantment" to enchant.display(level))
        } ?: sender.sendLang("command.subCommands.book.fail")
    }

    override val name: String
        get() = "book"

    init {
        CommandHandler.sub[name] = this
    }
}