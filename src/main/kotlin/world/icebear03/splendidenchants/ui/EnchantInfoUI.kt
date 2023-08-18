@file:Suppress("DEPRECATION")

package world.icebear03.splendidenchants.ui

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType.*
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.serverct.parrot.parrotx.function.variable
import org.serverct.parrot.parrotx.function.variables
import org.serverct.parrot.parrotx.mechanism.Reloadable
import org.serverct.parrot.parrotx.ui.MenuComponent
import org.serverct.parrot.parrotx.ui.config.MenuConfiguration
import org.serverct.parrot.parrotx.ui.feature.util.MenuFunctionBuilder
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import taboolib.platform.util.modifyMeta
import world.icebear03.splendidenchants.api.*
import world.icebear03.splendidenchants.api.internal.colorify
import world.icebear03.splendidenchants.enchant.EnchantFilter
import world.icebear03.splendidenchants.enchant.SplendidEnchant
import world.icebear03.splendidenchants.enchant.data.group
import world.icebear03.splendidenchants.enchant.data.groups
import world.icebear03.splendidenchants.enchant.data.isIn
import world.icebear03.splendidenchants.enchant.data.limitation.LimitType
import world.icebear03.splendidenchants.ui.internal.UIType
import world.icebear03.splendidenchants.ui.internal.record
import kotlin.collections.set

@MenuComponent("EnchantInfo")
object EnchantInfoUI {

    @Config("gui/enchant_info.yml")
    private lateinit var source: Configuration
    private lateinit var config: MenuConfiguration

    @Reloadable
    fun reload() {
        source.reload()
        config = MenuConfiguration(source)
    }

    fun open(player: Player, params: Map<String, Any?>) {
        open(
            player, params["enchant"] as SplendidEnchant,
            params["level"] as Int,
            params["checked"] as ItemStack,
            params["category"].toString()
        )
    }

    fun open(
        player: Player,
        enchant: SplendidEnchant,
        level: Int = enchant.maxLevel,
        checked: ItemStack = ItemStack(Material.AIR),
        category: String = "conflicts"
    ) {
        player.record(UIType.ENCHANT_INFO, "enchant" to enchant, "level" to level, "checked" to checked, "category" to category)
        player.openMenu<Linked<String>>(config.title().colorify()) {
            val (shape, templates) = config
            rows(shape.rows)
            val slots = shape["EnchantInfo:element"].toList()
            slots(slots)
            elements {
                when (category) {
                    "conflicts" -> enchant.limitations.limitations.mapNotNull { (type, identifier) ->
                        when (type) {
                            LimitType.CONFLICT_ENCHANT -> "enchant:$identifier"
                            LimitType.CONFLICT_GROUP -> "group:$identifier"
                            else -> null
                        }
                    }

                    "dependencies" -> enchant.limitations.limitations.mapNotNull { (type, identifier) ->
                        when (type) {
                            LimitType.DEPENDENCE_ENCHANT -> "enchant:$identifier"
                            LimitType.DEPENDENCE_GROUP -> "group:$identifier"
                            else -> null
                        }
                    }

                    "related" -> groups.values.filter { enchant.isIn(it) }.map { "group:${it.name}" }
                    else -> listOf()
                }
            }

            load(
                shape, templates, player,
                "EnchantInfo:available", "EnchantInfo:other", "EnchantInfo:limitations",
                "EnchantInfo:basic", "EnchantInfo:level", "EnchantInfo:related",
                "EnchantInfo:dependencies", "EnchantInfo:conflicts", "EnchantInfo:element",
                "EnchantInfo:favorite", "Previous", "Next"
            )
            pages(shape, templates)

            val template = templates.require("EnchantInfo:element")
            onGenerate { _, element, index, slot -> template(slot, index) { this["element"] = element } }
            onClick { event, element -> templates[event.rawSlot]?.handle(this, event, "element" to element) }

            val params = arrayOf(
                "player" to player,
                "enchant" to enchant,
                "level" to level,
                "checked" to checked,
                "category" to category
            )
            listOf("conflicts", "dependencies", "related").forEach {
                setSlots(shape, templates, "EnchantInfo:$it", listOf(), *params.clone().also { p -> p[4] = "category" to it })
            }
        }
    }

    @MenuComponent
    private val conflicts = MenuFunctionBuilder { onClick { (_, _, _, event, args) -> open(event.clicker, args) } }

    @MenuComponent
    private val dependencies = MenuFunctionBuilder { onClick { (_, _, _, event, args) -> open(event.clicker, args) } }

    @MenuComponent
    private val related = MenuFunctionBuilder { onClick { (_, _, _, event, args) -> open(event.clicker, args) } }

    @MenuComponent
    private val level = MenuFunctionBuilder {
        onBuild { (_, _, _, _, icon, args) ->
            val level = args["level"] as Int
            val enchant = args["enchant"] as SplendidEnchant
            icon.variable("params", enchant.variable.leveled.map { (variable, expression) ->
                "&b$variable &7> " + expression.calculate("level" to level)
            })
        }
        onClick { (_, _, _, event, args) ->
            var level = args["level"] as Int
            val enchant = args["enchant"] as SplendidEnchant
            level = when (event.clickEvent().click) {
                LEFT -> (level + 1).coerceAtMost(enchant.maxLevel)
                RIGHT -> (level - 1).coerceAtLeast(1)
                MIDDLE -> enchant.maxLevel
                else -> level
            }
            open(event.clicker, args.toMutableMap().also { it["level"] = level })
        }
    }

    @MenuComponent
    private val element = MenuFunctionBuilder {
        onBuild { (_, _, _, _, icon, args) ->
            val element = args["element"].toString()
            val parts = element.split(":")
            when (parts[0]) {
                "group" -> {
                    val group = group(parts[1])!!
                    icon.name = icon.name!!.split("||")[0]
                    icon.skull(group.skull)
                        .modifyMeta<ItemMeta> {
                            val lore = lore!!
                            val index = lore.indexOf("分隔符号")
                            this.lore = lore.subList(0, index)
                        }.variables {
                            listOf(
                                when (it) {
                                    "group" -> group.name
                                    "max_coexist" -> "${group.maxCoexist - 1}"
                                    else -> ""
                                }
                            )
                        }
                }

                "enchant" -> {
                    val enchant = splendidEt(parts[1])!!
                    val holders = enchant.displayer.holders()
                    icon.name = icon.name!!.split("||")[1]
                    icon.skull(enchant.rarity.skull)
                        .modifyMeta<ItemMeta> {
                            val lore = lore!!
                            val index = lore.indexOf("分隔符号")
                            this.lore = lore.subList(index + 1)
                        }.variables { variable -> listOf(holders[variable] ?: "") }
                }

                else -> icon
            }
        }

        onClick { (_, _, _, event, args) ->
            val player = event.clicker
            val element = args["element"].toString()
            val parts = element.split(":")
            when (parts[0]) {
                "group" -> {
                    EnchantFilter.clearFilters(player)
                    EnchantFilter.addFilter(player, EnchantFilter.FilterType.GROUP, parts[1], EnchantFilter.FilterStatement.ON)
                    EnchantSearchUI.open(player)
                }

                "enchant" -> open(player, splendidEt(parts[1])!!)
            }
        }
    }
}