package world.icebear03.splendidenchants.command.impl

import com.mcstarrysky.starrysky.command.CommandExecutor
import com.mcstarrysky.starrysky.i18n.sendLang
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.SimpleCommandBody
import taboolib.common.platform.command.subCommand
import taboolib.module.kether.isInt
import taboolib.platform.util.giveItem
import taboolib.platform.util.onlinePlayers
import world.icebear03.splendidenchants.api.book
import world.icebear03.splendidenchants.api.display
import world.icebear03.splendidenchants.api.drawEt
import world.icebear03.splendidenchants.command.CommandHandler
import world.icebear03.splendidenchants.enchant.data.rarities
import world.icebear03.splendidenchants.enchant.data.rarity

/**
 * SplendidEnchants
 * world.icebear03.splendidenchants.command.impl.CommandRandom
 *
 * @author mical
 * @since 2023/8/16 2:56 PM
 */
object CommandRandom : CommandExecutor {

    override val command: SimpleCommandBody
        get() = subCommand {
            dynamic("rarity") {
                suggestion<CommandSender> { _, _ -> rarities.map { it.key } + rarities.map { it.value.name } }
                dynamic("level", true) {
                    suggestionUncheck<CommandSender> { _, _ -> listOf("等级") }
                    dynamic("player", true) {
                        suggestion<CommandSender> { _, _ -> onlinePlayers.map { it.name } }
                        execute<CommandSender> { sender, args, _ ->
                            val rarity = rarity(args["rarity"])!!
                            val enchant = rarity.drawEt() ?: run {
                                sender.sendLang("command.subCommands.random.rarity")
                                return@execute
                            }
                            if (args.getOrNull("level")?.isInt() != true) {
                                sender.sendLang("command.subCommands.random.number")
                                return@execute
                            }
                            val level = args["level"].toInt().coerceAtLeast(1).coerceAtMost(enchant.maxLevel)
                            val receiver = args.getOrNull("player")?.let { Bukkit.getPlayer(it) } ?: (sender as? Player)
                            receiver?.let {
                                it.giveItem(enchant.book(level))
                                sender.sendLang("command.subCommands.random.sender", "name" to receiver.name, "enchantment" to enchant.display(level))
                                receiver.sendLang("command.subCommands.random.receiver", "enchantment" to enchant.display(level))
                            } ?: sender.sendLang("command.subCommands.random.fail")
                        }
                    }
                }
            }
        }

    override val name: String
        get() = "random"

    init {
        CommandHandler.sub[name] = this
    }
}