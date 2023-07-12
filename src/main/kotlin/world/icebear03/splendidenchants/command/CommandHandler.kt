@file:Suppress("deprecation")

package world.icebear03.splendidenchants.command

import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.component.CommandBase
import taboolib.common.platform.command.component.CommandComponent
import taboolib.common.platform.command.component.CommandComponentLiteral
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.function.pluginVersion
import taboolib.common.util.Strings
import taboolib.module.chat.RawMessage
import taboolib.module.nms.MinecraftVersion
import world.icebear03.splendidenchants.command.sub.*
import java.util.concurrent.ConcurrentHashMap

/**
 * SplendidEnchants
 * world.icebear03.splendidenchants.command.CommandHandler
 *
 * @author Mical
 * @since 2023/7/12 00:20
 */
@CommandHeader(name = "splendidenchants", aliases = ["se", "spe", "nereusopus", "no", "nerous"])
object CommandHandler {

    val sub = ConcurrentHashMap<String, CommandExecutor>()

    @CommandBody(aliases = ["help", "帮助"])
    val main = mainCommand {
        createTabooHelper()
    }

    @CommandBody(permission = "splendidenchants.admin", aliases = ["et", "附魔"])
    val enchant = CommandEnchant.command

    @CommandBody(permission = "splendidenchants.admin", aliases = ["bk", "附魔书"])
    val book = CommandBook.command

    @CommandBody(permission = "splendidenchants.admin", aliases = ["rl", "重载"])
    val reload = CommandReload.command

    @CommandBody(permission = "splendidenchants.admin", aliases = ["模式"])
    val mode = CommandMode.command

    @CommandBody(permission = "splendidenchants.admin", aliases = ["ran", "rd", "随机附魔书"])
    val random = CommandRandom.command

    @CommandBody(aliases = ["查询附魔"])
    val info = CommandInfo.command

    private fun CommandComponent.createTabooHelper() {
        execute<ProxyCommandSender> { sender, context, _ ->
            sender.sendMessage("")
            RawMessage()
                .append("  ").append("§6SplendidEnchants")
                .hoverText("§7SplendidEnchants 附魔扩展插件")
                .append(" ").append("§f${pluginVersion}")
                .hoverText(
                    """
                §7插件版本: §2${pluginVersion}
                §7游戏版本: §b${MinecraftVersion.minecraftVersion}
            """.trimIndent()
                ).sendTo(sender)
            sender.sendMessage("")
            RawMessage()
                .append("  §7命令: ").append("§f/splendidenchants §8[...]")
                .hoverText("§f/splendidenchants §8[...]")
                .suggestCommand("/splendidenchants ")
                .sendTo(sender)
            sender.sendMessage("  §7参数:")


            for (command in children.filterIsInstance<CommandComponentLiteral>()) {
                val name = command.aliases[0]
                var usage = sub[name]?.usage ?: ""
                if (usage.isNotEmpty()) {
                    usage += " "
                }
                val description = sub[name]?.description ?: "没有描述"
                command.children.filterIsInstance<CommandComponentLiteral>().map { it.aliases[0] }.forEach { }

                RawMessage()
                    .append("    §8- ").append("§f$name")
                    .hoverText("§f/splendidenchants $name $usage §8- §7$description")
                    .suggestCommand("/splendidenchants $name ")
                    .sendTo(sender)
                sender.sendMessage("      §7$description")
            }

            sender.sendMessage("")
        }

        if (this is CommandBase) {
            incorrectCommand { sender, ctx, _, state ->
                val name = ctx.self()
                var usage = sub[ctx.self()]?.usage ?: ""
                if (usage.isNotEmpty()) {
                    usage += " "
                }
                val description = sub[ctx.self()]?.description ?: "没有描述"
                when (state) {
                    1 -> {
                        sender.sendMessage("§6SplendidEnchants §7>> §7指令 §f$name §7参数不足.")
                        sender.sendMessage("§6SplendidEnchants §7>> §7正确用法:")
                        sender.sendMessage("§6SplendidEnchants §7>> §7§f/splendidenchants $name $usage§8- §7$description")
                    }

                    2 -> {
                        sender.sendMessage("§6SplendidEnchants §7>> §7指令 §f$name §7不存在.")
                        val similar = sub.keys.maxByOrNull { Strings.similarDegree(name, it) }!!
                        sender.sendMessage("§6SplendidEnchants §7>> §7你可能想要:")
                        sender.sendMessage("§6SplendidEnchants §7>> §7$similar")
                    }
                }
            }
            incorrectSender { sender, ctx ->
                sender.sendMessage("§6SplendidEnchants §7>> §7指令 §f${ctx.args().first()} §7只能由 §f玩家 §7执行.")
            }
        }
    }
}