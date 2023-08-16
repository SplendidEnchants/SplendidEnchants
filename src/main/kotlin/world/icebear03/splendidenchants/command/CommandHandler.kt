/*
 *  Copyright (C) <2023>  <Mical>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
import taboolib.module.chat.component
import taboolib.module.nms.MinecraftVersion
import world.icebear03.splendidenchants.api.i18n.asLangTextString
import world.icebear03.splendidenchants.api.i18n.sendLang
import world.icebear03.splendidenchants.command.impl.*
import java.util.concurrent.ConcurrentHashMap

/**
 * SplendidEnchants
 * world.icebear03.splendidenchants.command.CommandHandler
 *
 * @author mical
 * @since 2023/8/16 1:33 PM
 */
@CommandHeader(name = "splendidenchants", aliases = ["se", "spe", "nereusopus", "no", "nereus"])
object CommandHandler {

    val sub = ConcurrentHashMap<String, CommandExecutor>()
    val enchantNamesAndIds = mutableListOf<String>()

    @CommandBody
    val main = mainCommand {
        createTabooLegacyHelper()
    }

    @CommandBody(permission = "splendidenchants.book")
    val book = CommandBook.command

    @CommandBody(permission = "splendidenchants.enchant")
    val enchant = CommandEnchant.command

    @CommandBody(permission = "splendidenchants.random")
    val random = CommandRandom.command

    @CommandBody(permission = "splendidenchants.menu")
    val menu = CommandMenu.command

    @CommandBody(permission = "splendidenchants.reload")
    val reload = CommandReload.command

    private fun CommandComponent.createTabooLegacyHelper() {
        execute<ProxyCommandSender> { sender, _, _ ->
            val text = mutableListOf<String>()

            for (command in children.filterIsInstance<CommandComponentLiteral>()) {
                if (!sender.isOp) {
                    if (!sender.hasPermission(command.permission)) {
                        continue
                    } else {
                        if (command.hidden) continue
                    }
                }
                val name = command.aliases[0]
                var usage = sub[name]?.usage(sender) ?: sender.asLangTextString("command.no-usage")
                if (usage.isNotEmpty()) {
                    usage += " "
                }
                val description = sub[name]?.desc(sender) ?: sender.asLangTextString("command.no-description")

                text += sender.asLangTextString("command.sub", "name" to name, "usage" to usage, "description" to description)
            }

            sender.asLangTextString("command.helper", "pluginVersion" to pluginVersion, "minecraftVersion" to MinecraftVersion.minecraftVersion)
                .replace("{subCommands}", text.joinToString(separator = "[](br)", prefix = "", postfix = ""))
                .component()
                .build { colored() }
                .sendTo(sender)
        }

        if (this is CommandBase) {
            incorrectCommand { sender, ctx, _, state ->

                val input = ctx.args().first()
                val name = children.filterIsInstance<CommandComponentLiteral>().firstOrNull { it.aliases.contains(input) }?.aliases?.get(0) ?: input
                var usage = sub[name]?.usage(sender) ?: sender.asLangTextString("command.no-usage")
                if (usage.isNotEmpty()) {
                    usage += " "
                }
                val description = sub[name]?.desc(sender) ?: sender.asLangTextString("command.no-description")
                when (state) {
                    1 -> {
                        sender.sendLang("command.argument-missing", "name" to name, "usage" to usage, "description" to description, prefix = false)
                    }

                    2 -> {
                        if (ctx.args().size > 1) {
                            sender.sendLang("command.argument-wrong", "name" to name, "usage" to usage, "description" to description, prefix = false)
                        } else {
                            val similar = sub.keys
                                .asSequence()
                                .map { children.filterIsInstance<CommandComponentLiteral>().firstOrNull { c -> c.aliases[0] == it } }
                                .filterNotNull()
                                .filterNot { it.hidden }
                                .filter { sender.hasPermission(it.permission) }
                                .maxByOrNull { Strings.similarDegree(name, it.aliases[0]) }!!
                                .aliases[0]
                            sender.sendLang("command.argument-unknown", "name" to name, "similar" to similar, prefix = false)
                        }
                    }
                }
            }
            incorrectSender { sender, ctx ->
                sender.sendLang("command.incorrect-sender", "name" to ctx.args().first(), prefix = false)
            }
        }
    }
}