@file:Suppress("deprecation")
package world.icebear03.splendidenchants.command

import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.*
import taboolib.common.platform.command.component.CommandBase
import taboolib.common.platform.command.component.CommandComponent
import taboolib.common.platform.command.component.CommandComponentDynamic
import taboolib.common.platform.command.component.CommandComponentLiteral
import taboolib.common.platform.function.pluginVersion
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
            """.trimIndent()).sendTo(sender)
            sender.sendMessage("")
            RawMessage()
                .append("  §7命令: ").append("§f/splendidenchants §8[...]")
                .hoverText("§f/splendidenchants §8[...]")
                .suggestCommand("/splendidenchants ")
                .sendTo(sender)
            sender.sendMessage("  §7参数:")


            for (command in children.filterIsInstance<CommandComponentLiteral>()) {
                val name = command.aliases[0]
                val description = sub[name]?.description ?: "没有描述"
                val args = StringBuilder()
                command.children.filterIsInstance<CommandComponentLiteral>().map { it.aliases[0] }.forEach {  }

                RawMessage()
                    .append("    §8- ").append("§f$name")
                    .hoverText("§f/splendidenchants $name §8- §7$description")
                    .suggestCommand("/splendidenchants $name ")
                    .sendTo(sender)
                sender.sendMessage("      §7$description")
            }

            sender.sendMessage("")
        }

        if (this is CommandBase) {
            incorrectCommand { sender, ctx, _, state ->
                when (state) {
                    1 -> {
                        sender.sendMessage("§8[§6SplendidEnchants§8] §7指令 §f${ctx.self()} §7参数不足.")
                        sender.sendMessage("§8[§6SplendidEnchants§8] §7正确用法:")
                        sender.sendMessage("§8[§6SplendidEnchants§8] §7§f/splendidenchants ${ctx.self()} §8- §7${sub[ctx.self()]?.description ?: "没有描述"}")
                    }
                    2 -> {
                        sender.sendMessage("§8[§6SplendidEnchants§8] §7指令 §f${ctx.self()} §7不存在.")
                        val similar = getMostSimilarCommand(ctx.self())
                        if (similar != null) {
                            sender.sendMessage("§8[§6SplendidEnchants§8] §7你可能想要:")
                            sender.sendMessage("§8[§6SplendidEnchants§8] §7$similar")
                        }
                    }
                }
            }
        }
    }

    private fun compare(str: String, target: String): Int {
        val d: Array<IntArray>
        val n = str.length
        val m = target.length
        var j: Int
        var ch1: Char
        var ch2: Char
        var temp: Int
        if (n == 0) {
            return m
        }
        if (m == 0) {
            return n
        }
        d = Array(n + 1) { IntArray(m + 1) }
        var i = 0
        while (i <= n) {
            d[i][0] = i
            i++
        }
        j = 0
        while (j <= m) {
            d[0][j] = j
            j++
        }
        i = 1
        while (i <= n) {
            ch1 = str[i - 1]
            j = 1
            while (j <= m) {
                ch2 = target[j - 1]
                temp = if (ch1 == ch2 || ch1.code == ch2.code + 32 || ch1.code + 32 == ch2.code) {
                    0
                } else {
                    1
                }
                d[i][j] = (d[i - 1][j] + 1).coerceAtMost(d[i][j - 1] + 1).coerceAtMost(d[i - 1][j - 1] + temp)
                j++
            }
            i++
        }
        return d[n][m]
    }

    private fun getSimilarityRatio(str: String, target: String): Float {
        val max = Math.max(str.length, target.length)
        return 1 - compare(str, target).toFloat() / max
    }

    private fun getMostSimilarCommand(command: String): String? {
        var result: String? = null
        var similarity = 0f
        for (subCommand in sub.keys()) {
            val t = getSimilarityRatio(command, subCommand)
            if (t > similarity) {
                similarity = t
                result = subCommand
            }
        }
        return result
    }
}