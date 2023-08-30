package world.icebear03.splendidenchants.command.impl

import com.mcstarrysky.starrysky.command.CommandExecutor
import com.mcstarrysky.starrysky.i18n.sendLang
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.SimpleCommandBody
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.command.suggestPlayers
import world.icebear03.splendidenchants.api.*
import world.icebear03.splendidenchants.command.CommandHandler
import world.icebear03.splendidenchants.enchant.SplendidEnchant

object CommandEnchant : CommandExecutor {

    override val command: SimpleCommandBody
        get() = subCommand {
            dynamic("enchant") {
                suggestion<CommandSender> { _, _ -> CommandHandler.enchantNamesAndIds }
                execute<CommandSender> { sender, args, _ -> handle(sender, null, splendidEt(args["enchant"])!!) }
                dynamic("level", true) {
                    suggestion<Player> level@{ _, cmd ->
                        val maxLevel = (splendidEt(cmd["enchant"])?.maxLevel ?: return@level listOf())
                        buildList { repeat(maxLevel + 1) { add("$it") } }
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
            val item = receiver.inventory.itemInMainHand
            if (item.isNull) {
                sender.sendLang("command.subCommands.enchant.empty")
                return
            }
            val state = if (level == 0) "移除" else "添加"
            if (level == 0) item.removeEt(enchant)
            else item.addEt(enchant, level)
            sender.sendLang(
                "command.subCommands.enchant.sender",
                "name" to receiver.name,
                "state" to state,
                "enchantment" to enchant.display(level)
            )
            receiver.sendLang("command.subCommands.enchant.receiver", "state" to state, "enchantment" to enchant.display(level))
        } ?: sender.sendLang("command.subCommands.enchant.fail")
    }

    override val name: String
        get() = "enchant"

    init {
        CommandHandler.sub[name] = this
    }
}