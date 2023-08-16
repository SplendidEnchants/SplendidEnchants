package world.icebear03.splendidenchants.command.impl

import com.mcstarrysky.starrysky.command.CommandExecutor
import com.mcstarrysky.starrysky.i18n.sendLang
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.SimpleCommandBody
import taboolib.common.platform.command.subCommand
import taboolib.platform.util.isAir
import taboolib.platform.util.onlinePlayers
import world.icebear03.splendidenchants.api.addEt
import world.icebear03.splendidenchants.api.display
import world.icebear03.splendidenchants.api.removeEt
import world.icebear03.splendidenchants.api.splendidEt
import world.icebear03.splendidenchants.command.CommandHandler

/**
 * SplendidEnchants
 * world.icebear03.splendidenchants.command.impl.CommandEnchant
 *
 * @author mical
 * @since 2023/8/16 2:17 PM
 */
object CommandEnchant : CommandExecutor {

    override val command: SimpleCommandBody
        get() = subCommand {
            dynamic("enchant") {
                suggestion<CommandSender> { _, _ -> CommandHandler.enchantNamesAndIds }
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
                                    sender.sendLang("command.subCommands.enchant.empty")
                                    return@execute
                                }
                                val state = if (level == 0) "移除" else "添加"
                                if (level == 0) item.removeEt(enchant)
                                else item.addEt(enchant, level)
                                sender.sendLang("command.subCommands.enchant.sender", "name" to receiver.name, "state" to state, "enchantment" to enchant.display(level))
                                receiver.sendLang("command.subCommands.enchant.receiver", "state" to state, "enchantment" to enchant.display(level))
                            } ?: sender.sendLang("command.subCommands.enchant.fail")
                        }
                    }
                }
            }
        }

    override val name: String
        get() = "enchant"

    init {
        CommandHandler.sub[name] = this
    }
}