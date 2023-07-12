package world.icebear03.splendidenchants.command.sub

import org.bukkit.entity.Player
import taboolib.common.platform.command.SimpleCommandBody
import taboolib.common.platform.command.int
import taboolib.common.platform.command.subCommand
import taboolib.module.kether.isInt
import taboolib.platform.util.isAir
import world.icebear03.splendidenchants.api.EnchantAPI
import world.icebear03.splendidenchants.api.ItemAPI
import world.icebear03.splendidenchants.command.CommandExecutor
import world.icebear03.splendidenchants.command.CommandHandler
import world.icebear03.splendidenchants.enchant.EnchantLoader

/**
 * SplendidEnchants
 * world.icebear03.splendidenchants.command.sub.CommandEnchant
 *
 * @author Mical
 * @since 2023/7/12 00:41
 */
object CommandEnchant : CommandExecutor {

    override val command: SimpleCommandBody = subCommand {
        dynamic("enchant") {
            suggestionUncheck<Player> { _, _ ->
                // FIXME
                EnchantLoader.enchantById.keys().toList().toMutableList().also {
                    it.addAll(EnchantLoader.enchantByName.keys().toList())
                }
            }
            dynamic("level") {
                suggestionUncheck<Player> { _, ctx ->
                    val enchant = EnchantAPI.getSplendidEnchant(ctx["enchant"]) ?: return@suggestionUncheck (0..9).toList().map { it.toString() }
                    return@suggestionUncheck (0..enchant.maxLevel).toList().map { it.toString() }
                }

                execute<Player> { sender, ctx, _ ->
                    val enchant = EnchantAPI.getSplendidEnchant(ctx["enchant"])
                    val level = ctx["level"]
                    if (enchant == null) {
                        sender.sendMessage("§6SplendidEnchants §7>> §7附魔 §f${ctx["enchant"]} §7不存在.")
                        return@execute
                    }
                    if (!level.isInt()) {
                        sender.sendMessage("§6SplendidEnchants §7>> §7等级必须为数字.")
                        return@execute
                    }
                    if (level.toInt() < 0) {
                        sender.sendMessage("§6SplendidEnchants §7>> §7等级必须大于等于0(0为清除附魔).")
                        return@execute
                    }
                    val item = sender.inventory.itemInMainHand
                    if (item.isAir) {
                        sender.sendMessage("§6SplendidEnchants §7>> §7你必须手持物品才可以对物品附魔.")
                        return@execute
                    }

                    when {
                        level.toInt() > 0 -> {
                            ItemAPI.addEnchant(item, enchant, level.toInt())
                            sender.sendMessage("§6SplendidEnchants §7>> §7已成功为物品添加附魔 ${enchant.rarity.color}${enchant.basicData.name}§7, 等级 §f$level§7.")
                        }
                        level.toInt() == 0 -> {
                            ItemAPI.removeEnchant(item, enchant)
                            sender.sendMessage("§6SplendidEnchants §7>> §7已成功为物品清除附魔 ${enchant.rarity.color}${enchant.basicData.name}§7.")
                        }
                    }
                }
            }
        }
    }

    override val name: String
        get() = "enchant"

    override val description: String
        get() = "为手中物品附魔"

    override val usage: String
        get() = "§7[§8附魔ID/名称§7] §7[§8附魔等级§7]"

    init {
        CommandHandler.sub[name] = this
    }
}