package world.icebear03.splendidenchants.ui

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.serverct.parrot.parrotx.mechanism.Reloadable
import org.serverct.parrot.parrotx.ui.MenuComponent
import org.serverct.parrot.parrotx.ui.config.MenuConfiguration
import org.serverct.parrot.parrotx.ui.feature.util.MenuFunctionBuilder
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import taboolib.platform.util.modifyMeta
import world.icebear03.splendidenchants.api.internal.colorify
import world.icebear03.splendidenchants.api.load
import world.icebear03.splendidenchants.api.pages
import world.icebear03.splendidenchants.enchant.SplendidEnchant
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

                    "groups" -> groups.values.filter { enchant.isIn(it) }.map { "group:${it.name}" }
                    else -> listOf()
                }
            }

            load(
                shape, templates, true, player,
                "EnchantInfo:available", "EnchantInfo:other", "EnchantInfo:limitations",
                "EnchantInfo:basic", "EnchantInfo:level", "EnchantInfo:groups",
                "EnchantInfo:dependencies", "EnchantInfo:conflicts", "EnchantInfo:element",
                "EnchantInfo:favorite", "Previous", "Next"
            )
            pages(shape, templates)

            val template = templates.require("EnchantInfo:element")
            onGenerate { _, element, index, slot -> template(slot, index) { this["element"] = element } }

            val params = arrayOf(
                "player" to player,
                "enchant" to enchant,
                "level" to level,
                "checked" to checked,
                "category" to category
            )
        }
    }

    @MenuComponent
    private val element = MenuFunctionBuilder {
        onBuild { (_, _, _, _, icon, args) ->
            val element = args["element"].toString()
            val parts = element.split(":")
            when (parts[0]) {
                "group" -> {
                    //TODO 应当自定义
                    icon.modifyMeta<ItemMeta> {
                        setDisplayName("")
                    }
                }

                "enchant" -> {
                    icon
                }

                else -> icon
            }
        }
    }
}