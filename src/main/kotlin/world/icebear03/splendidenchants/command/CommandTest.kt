package world.icebear03.splendidenchants.command

import org.bukkit.entity.Player
import taboolib.common.platform.command.*
import taboolib.common.platform.command.component.CommandComponent
import taboolib.common.platform.command.component.CommandComponentDynamic
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
        createHelper<Any>()
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

    inline fun <CommandComponentLiteral, reified ProxyCommandSender> CommandComponent.createHelper(checkPermissions: Boolean = true) {
        execute<ProxyCommandSender> { sender, context, _ ->
            val command = context.command
            val builder = StringBuilder("§cUsage: /${command.name}")
            var newline = false

            fun check(children: List<CommandComponent>): List<CommandComponent> {
                // 检查权限
                val filterChildren = if (checkPermissions) {
                    children.filter { sender.hasPermission(it.permission) }
                } else {
                    children
                }
                // 过滤隐藏
                return filterChildren.filter { it !is CommandComponentLiteral || !it.hidden }
            }

            fun space(space: Int): String {
                return (1..space).joinToString("") { " " }
            }

            fun print(compound: CommandComponent, index: Int, size: Int, offset: Int = 8, level: Int = 0, end: Boolean = false, optional: Boolean = false) {
                var option = optional
                var comment = 0
                when (compound) {
                    is CommandComponentLiteral -> {
                        if (size == 1) {
                            builder.append(" ").append("§c${compound.aliases[0]}")
                        } else {
                            newline = true
                            builder.appendLine()
                            builder.append(space(offset))
                            if (level > 1) {
                                builder.append(if (end) " " else "§7│")
                            }
                            builder.append(space(level))
                            if (index + 1 < size) {
                                builder.append("§7├── ")
                            } else {
                                builder.append("§7└── ")
                            }
                            builder.append("§c${compound.aliases[0]}")
                        }
                        option = false
                        comment = compound.aliases[0].length
                    }
                    is CommandComponentDynamic -> {
                        val value = if (compound.comment.startsWith("@")) {
                            sender.asLangText(compound.comment.substring(1))
                        } else {
                            compound.comment
                        }
                        comment = if (compound.optional || option) {
                            option = true
                            builder.append(" ").append("§8[<$value>]")
                            compound.comment.length + 4
                        } else {
                            builder.append(" ").append("§7<$value>")
                            compound.comment.length + 2
                        }
                    }
                }
                if (level > 0) {
                    comment += 1
                }
                val checkedChildren = check(compound.children)
                checkedChildren.forEachIndexed { i, children ->
                    // 因 literal 产生新的行
                    if (newline) {
                        print(children, i, checkedChildren.size, offset, level + comment, end, option)
                    } else {
                        val length = if (offset == 8) command.name.length + 1 else comment + 1
                        print(children, i, checkedChildren.size, offset + length, level, end, option)
                    }
                }
            }
            val checkedChildren = check(context.commandCompound.children)
            val size = checkedChildren.size
            checkedChildren.forEachIndexed { index, children ->
                print(children, index, size, end = index + 1 == size)
            }
            builder.lines().forEach {
                sender.sendMessage(it)
            }
        }
    }
}