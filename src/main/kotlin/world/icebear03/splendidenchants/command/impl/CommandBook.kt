package world.icebear03.splendidenchants.command.impl

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.SimpleCommandBody
import taboolib.common.platform.command.subCommand
import taboolib.platform.util.giveItem
import taboolib.platform.util.onlinePlayers
import world.icebear03.splendidenchants.api.book
import world.icebear03.splendidenchants.api.display
import world.icebear03.splendidenchants.api.i18n.sendLang
import world.icebear03.splendidenchants.api.splendidEt
import world.icebear03.splendidenchants.command.CommandExecutor
import world.icebear03.splendidenchants.command.CommandHandler

/**
 * SplendidEnchants
 * world.icebear03.splendidenchants.command.impl.CommandBook
 *
 * @author mical
 * @since 2023/8/16 1:46 PM
 */
object CommandBook : CommandExecutor {

    override val command: SimpleCommandBody
        get() = subCommand {
            dynamic("enchant") {
                suggestion<CommandSender> { _, _ -> CommandHandler.enchantNamesAndIds }
                dynamic("level", true) {
                    suggestion<Player> level@{ _, cmd ->
                        val maxLevel = (splendidEt(cmd["enchant"])?.maxLevel ?: return@level listOf("附魔不存在"))
                        buildList { repeat(maxLevel) { add("${it + 1}") } }
                    }
                    dynamic("player", true) {
                        suggestion<CommandSender> { _, _ -> onlinePlayers.map { it.name } }
                        execute<CommandSender> { sender, args, _ ->
                            val enchant = splendidEt(args["enchant"])!!
                            val level = args.getOrNull("level")?.toInt() ?: enchant.maxLevel
                            val receiver = args.getOrNull("player")?.let { Bukkit.getPlayer(it) } ?: (sender as? Player)
                            receiver?.let {
                                it.giveItem(enchant.book(level))
                                sender.sendLang("command.subCommands.book.sender", "name" to receiver.name)
                                receiver.sendLang("command.subCommands.book.receiver", "enchantment" to enchant.display(level))
                            } ?: sender.sendLang("command.subCommands.book.fail")
                        }
                    }
                }
            }
        }

    override val name: String
        get() = "book"

    init {
        CommandHandler.sub[name] = this
    }
}