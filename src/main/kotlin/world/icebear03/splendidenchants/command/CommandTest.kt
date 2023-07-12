package world.icebear03.splendidenchants.command

import org.bukkit.entity.Player
import taboolib.common.platform.command.*
import taboolib.expansion.createHelper

/**
 * SplendidEnchants
 * world.icebear03.splendidenchants.command.CommandTest
 *
 * @author Mical
 * @since 2023/7/12 19:20
 */
@CommandHeader("test")
object CommandTest {

    @CommandBody
    val main = mainCommand {
        createHelper()
    }

    @CommandBody
    val dev = subCommand {
        dynamic("测试数值1") {
            dynamic("你妈死了", optional = true) {
                restrictInt()
                execute<Player> { sender, _, argument ->
                    sender.sendMessage(argument)
                }
            }
            execute<Player> { sender, _, argument ->
                sender.sendMessage(argument)
            }
        }
    }

    @CommandBody
    val dev2 = subCommand {
        literal("three", "two") {
            dynamic(comment = "T") {
                execute<Player> { sender, _, argument ->
                    sender.sendMessage(argument)
                }
            }
        }
    }
}