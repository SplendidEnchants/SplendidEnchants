@file:Suppress("deprecation")
package world.icebear03.splendidenchants.command

import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.function.pluginVersion
import taboolib.module.chat.RawMessage
import taboolib.module.lang.asLangText
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
        execute<ProxyCommandSender> { sender, _, _ ->
            generateMainHelper(sender)
        }
        incorrectCommand { sender, ctx, _, _ ->
            sender.sendMessage("§8[§6SplendidEnchants§8] §7指令 §f${ctx.self()} §7不存在.")
            val similar = getMostSimilarCommand(ctx.self())
            if (similar != null) {
                sender.sendMessage("§8[§6SplendidEnchants§8] §7你可能想要:")
                sender.sendMessage("§8[§6SplendidEnchants§8] §7$similar")
            }
        }
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

    private fun generateMainHelper(proxySender: ProxyCommandSender) {
        proxySender.sendMessage("")
        RawMessage()
            .append("  ").append("§6SplendidEnchants")
            .hoverText("§7SplendidEnchants 附魔扩展插件")
            .append(" ").append("§f${pluginVersion}")
            .hoverText(
                """
                §7插件版本: §2${pluginVersion}
                §7游戏版本: §b${MinecraftVersion.minecraftVersion}
            """.trimIndent()
            ).sendTo(proxySender)
        proxySender.sendMessage("")
        RawMessage()
            .append("  §7${proxySender.asLangText("Command-Help-Type")}: ").append("§f/splendidenchants §8[...]")
            .hoverText("§f/splendidenchants §8[...]")
            .suggestCommand("/splendidenchants ")
            .sendTo(proxySender)
        proxySender.sendMessage("  §7${proxySender.asLangText("Command-Help-Args")}:")

        fun displayArg(name: String, desc: String) {
            RawMessage()
                .append("    §8- ").append("§f$name")
                .hoverText("§f/splendidenchants $name §8- §7$desc")
                .suggestCommand("/splendidenchants $name ")
                .sendTo(proxySender)
            proxySender.sendMessage("      §7$desc")
        }
        sub.forEach { (name, executor) -> displayArg(name, executor.description(proxySender)) }
        proxySender.sendMessage("")
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