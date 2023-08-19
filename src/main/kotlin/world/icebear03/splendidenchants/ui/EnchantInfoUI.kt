@file:Suppress("DEPRECATION")

package world.icebear03.splendidenchants.ui

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot.*
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
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
import world.icebear03.splendidenchants.enchant.data.limitation.CheckType
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
        player.openMenu<Linked<String>>(
            config.title()
                .replace("[enchant_display_roman]", enchant.display(level))
                .colorify()
        ) {
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
                "EnchantInfo:favorite", "EnchantInfo:minus", "EnchantInfo:plus", "Previous", "Next"
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
            setSlots(shape, templates, "EnchantInfo:available", listOf(), *params)
            setSlots(shape, templates, "EnchantInfo:basic", listOf(), *params)
            setSlots(shape, templates, "EnchantInfo:limitations", listOf(), *params)
            setSlots(shape, templates, "EnchantInfo:other", listOf(), *params)
            setSlots(shape, templates, "EnchantInfo:favorite", listOf(), *params)
            setSlots(shape, templates, "EnchantInfo:level", listOf(), *params)
            setSlots(shape, templates, "EnchantInfo:minus", listOf(), *params)
            setSlots(shape, templates, "EnchantInfo:plus", listOf(), *params)

            onClick { event ->
                event.isCancelled = true
                if (event.rawSlot !in shape) {
                    val item = event.currentItem ?: return@onClick
                    open(player, enchant, level, item, category)
                }
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
            icon.variables {
                when (it) {
                    "params" -> enchant.variable.leveled.map { (variable, expression) ->
                        "&b$variable &7> " + expression.calculate("level" to level)
                    }

                    "roman" -> listOf(level.roman())
                    else -> listOf()
                }
            }
        }
        onClick { (_, _, _, event, args) ->
            val enchant = args["enchant"] as SplendidEnchant
            open(event.clicker, args.toMutableMap().also { it["level"] = enchant.maxLevel })
        }
    }

    @MenuComponent
    private val minus = MenuFunctionBuilder {
        onClick { (_, _, _, event, args) ->
            val level = args["level"] as Int
            open(event.clicker, args.toMutableMap().also { it["level"] = (level - 1).coerceAtLeast(1) })
        }
    }

    @MenuComponent
    private val plus = MenuFunctionBuilder {
        onClick { (_, _, _, event, args) ->
            val level = args["level"] as Int
            val enchant = args["enchant"] as SplendidEnchant
            open(event.clicker, args.toMutableMap().also { it["level"] = (level + 1).coerceAtMost(enchant.maxLevel) })
        }
    }


    @MenuComponent
    private val basic = MenuFunctionBuilder {
        onBuild { (_, _, _, _, icon, args) ->
            val enchant = args["enchant"] as SplendidEnchant
            val holders = enchant.displayer.holders(enchant.maxLevel)
            icon.variables { variable -> listOf(holders[variable] ?: "") }
                .skull(enchant.rarity.skull)
        }
    }

    @MenuComponent
    private val limitations = MenuFunctionBuilder {
        onBuild { (_, _, _, _, icon, args) ->
            val enchant = args["enchant"] as SplendidEnchant
            val player = args["player"] as Player
            val limits = enchant.limitations.limitations
            val conflicts = limits.filter { it.first.toString().contains("CONFLICT") }.ifEmpty { listOf("" to "无") }.joinToString("; ") { it.second }
            val dependencies = limits.filter { it.first.toString().contains("DEPENDENCE") }.ifEmpty { listOf("" to "无") }.joinToString("; ") { it.second }
            val perms = limits.filter { it.first == LimitType.PERMISSION }.map { it.second }
            val permission = if (perms.none { !player.hasPermission(it) }) "&a✓" else "&c✗"
            val disableWorlds = enchant.basicData.disableWorlds.ifEmpty { listOf("无") }
            val activeSlots = enchant.targets.map { it.activeSlots }.flatten().toSet().joinToString("; ") {
                when (it) {
                    HAND -> "主手"
                    OFF_HAND -> "副手"
                    FEET -> "靴子"
                    LEGS -> "护腿"
                    CHEST -> "盔甲"
                    HEAD -> "头盔"
                }
            }
            icon.variables {
                listOf(
                    when (it) {
                        "targets" -> enchant.targets.joinToString("; ") { target -> target.name }
                        "conflicts" -> conflicts
                        "dependencies" -> dependencies
                        "permission" -> permission
                        "disable_worlds" -> disableWorlds.joinToString("; ")
                        "active_slots" -> activeSlots
                        else -> ""
                    }
                )
            }
        }
    }

    @MenuComponent
    private val other = MenuFunctionBuilder {
        onBuild { (_, _, _, _, icon, args) ->
            val enchant = args["enchant"] as SplendidEnchant
            var attainWays = ""
            if (enchant.alternativeData.isDiscoverable &&
                enchant.alternativeData.weight > 0 &&
                enchant.rarity.weight > 0
            ) attainWays += "&d附魔台 &e战利品箱"
            if (enchant.alternativeData.isTradeable &&
                enchant.isIn("可交易附魔")
            ) {
                if (attainWays.isNotBlank()) attainWays += " "
                attainWays += "&6村民"
            }
            icon.variables {
                listOf(
                    when (it) {
                        "attain_ways" -> attainWays
                        "grindstoneable" -> if (enchant.alternativeData.grindstoneable) "&a✓" else "&c✗"
                        "treasure" -> if (enchant.isTreasure) "&a✓" else "&c✗"
                        "curse" -> if (enchant.isCursed) "&a✓" else "&c✗"
                        else -> ""
                    }
                )
            }
        }
    }

    @MenuComponent
    private val available = MenuFunctionBuilder {
        onBuild { (_, _, _, _, icon, args) ->
            val enchant = args["enchant"] as SplendidEnchant
            val checked = args["checked"] as ItemStack
            if (checked.isNull) {
                return@onBuild icon.variables {
                    listOf(
                        when (it) {
                            "state" -> "N/A"
                            "reasons" -> "无"
                            else -> ""
                        }
                    )
                }
            }
            val level = checked.etLevel(enchant)
            val player = args["player"] as Player
            val result = enchant.limitations.checkAvailable(CheckType.ANVIL, checked, player)
            val state = if (level > 0) "&a已安装 " + if (level < enchant.maxLevel) "可升级" else "最高级"
            else if (result.first) "&e可安装"
            else "&c不可安装"
            val reasons = result.second.ifEmpty { "无" }

            icon.variables {
                listOf(
                    when (it) {
                        "state" -> state
                        "reasons" -> reasons
                        else -> ""
                    }
                )
            }
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
                                    "max_coexist" -> "${group.maxCoexist - if (args["category"] == "conflicts") 1 else 0}"
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