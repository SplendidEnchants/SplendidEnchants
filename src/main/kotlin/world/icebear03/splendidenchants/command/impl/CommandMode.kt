package world.icebear03.splendidenchants.command.impl

import com.mcstarrysky.starrysky.command.CommandExecutor
import com.mcstarrysky.starrysky.i18n.sendLang
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import taboolib.common.platform.command.SimpleCommandBody
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.command.suggestPlayers
import world.icebear03.splendidenchants.command.CommandHandler
import world.icebear03.splendidenchants.player.internal.MenuMode
import world.icebear03.splendidenchants.player.menuMode

object CommandMode : CommandExecutor {

    override val command: SimpleCommandBody
        get() = subCommand {
            dynamic("mode", true) {
                suggestion<CommandSender> { _, _ -> listOf("作弊", "普通", "cheat", "normal") }
                execute<CommandSender> { sender, args, _ -> handle(sender, args["mode"], sender.name) }
                dynamic("player", true) {
                    suggestPlayers()
                    execute<CommandSender> { sender, args, _ -> handle(sender, args["mode"], args["player"]) }
                }
            }
        }

    fun handle(sender: CommandSender, mode: String, who: String?) {
        (who?.let { Bukkit.getPlayer(it) } ?: (sender as? Player))?.let { receiver ->
            receiver.menuMode = when (mode) {
                "作弊", "cheat" -> MenuMode.CHEAT
                "普通", "normal" -> MenuMode.NORMAL
                else -> MenuMode.NORMAL
            }
            receiver.sendLang("command.subCommands.mode.receiver", "mode" to mode)
            sender.sendLang("command.subCommands.mode.sender", "mode" to mode, "name" to receiver.name)
        } ?: sender.sendLang("command.subCommands.mode.fail")
    }

    override val name: String
        get() = "mode"

    init {
        CommandHandler.sub[name] = this
    }
}