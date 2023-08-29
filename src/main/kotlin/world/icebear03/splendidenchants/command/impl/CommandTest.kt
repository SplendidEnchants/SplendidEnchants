package world.icebear03.splendidenchants.command.impl

import com.mcstarrysky.starrysky.command.CommandExecutor
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import taboolib.common.platform.command.SimpleCommandBody
import taboolib.common.platform.command.subCommand
import taboolib.common5.cdouble
import world.icebear03.splendidenchants.command.CommandHandler

object CommandTest : CommandExecutor {

    override val command: SimpleCommandBody
        get() = subCommand {
            dynamic("y") {
                execute<Player> { sender, args, _ ->
                    println(sender.velocity)
                    sender.velocity = Vector(0.0, args["y"].cdouble, 0.0)
                }
            }
        }

    override val name: String
        get() = "test"

    init {
        CommandHandler.sub[name] = this
    }
}