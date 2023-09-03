package world.icebear03.splendidenchants.ui

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.serverct.parrot.parrotx.function.variables
import org.serverct.parrot.parrotx.mechanism.Reloadable
import org.serverct.parrot.parrotx.ui.MenuComponent
import org.serverct.parrot.parrotx.ui.config.MenuConfiguration
import org.serverct.parrot.parrotx.ui.feature.util.MenuFunctionBuilder
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import world.icebear03.splendidenchants.api.internal.colorify
import world.icebear03.splendidenchants.api.load
import world.icebear03.splendidenchants.api.pages
import world.icebear03.splendidenchants.api.skull
import world.icebear03.splendidenchants.api.splendidEts
import world.icebear03.splendidenchants.enchant.EnchantFilter
import world.icebear03.splendidenchants.enchant.data.Rarity
import world.icebear03.splendidenchants.enchant.data.rarities
import world.icebear03.splendidenchants.ui.internal.UIType
import world.icebear03.splendidenchants.ui.internal.record
import kotlin.collections.set


@MenuComponent("FilterRarity")
object FilterRarityUI {

    @Config("gui/filter_rarity.yml")
    private lateinit var source: Configuration
    private lateinit var config: MenuConfiguration

    @Reloadable
    fun reload() {
        source.reload()
        config = MenuConfiguration(source)
    }

    fun open(player: Player) {
        player.record(UIType.FILTER_RARITY)
        player.openMenu<Linked<Rarity>>(config.title().colorify()) {
            val (shape, templates) = config
            rows(shape.rows)
            val slots = shape["FilterRarity:filter"].toList()
            slots(slots)
            elements { rarities.values.toList() }

            load(shape, templates, player, "FilterRarity:filter", "Previous", "Next")
            pages(shape, templates)

            val template = templates.require("FilterRarity:filter")
            onGenerate { _, element, index, slot ->
                template(slot, index) {
                    this["rarity"] = element
                    this["player"] = player
                }
            }
            onClick { event, element -> templates[event.rawSlot]?.handle(this, event, "rarity" to element) }
        }
    }

    @MenuComponent
    private val filter = MenuFunctionBuilder {
        onBuild { (_, _, _, _, icon, args) ->
            val rarity = args["rarity"] as Rarity
            val player = args["player"] as Player

            when (EnchantFilter.getStatement(player, EnchantFilter.FilterType.RARITY, rarity)) {
                EnchantFilter.FilterStatement.ON -> icon.type = Material.LIME_STAINED_GLASS_PANE
                EnchantFilter.FilterStatement.OFF -> icon.type = Material.RED_STAINED_GLASS_PANE
                else -> {}
            }

            icon.variables {
                when (it) {
                    "name", "rarity_display" -> listOf(rarity.display())
                    "amount" -> listOf(splendidEts(rarity).size.toString())
                    else -> emptyList()
                }
            }.skull(rarity.skull)
        }

        onClick { (_, _, _, event, args) ->
            val clickType = event.clickEvent().click
            val player = event.clicker
            val rarity = args["rarity"] as Rarity

            when (clickType) {
                ClickType.LEFT, ClickType.RIGHT -> {
                    EnchantFilter.clearFilter(player, EnchantFilter.FilterType.RARITY, rarity)
                    EnchantFilter.addFilter(
                        player, EnchantFilter.FilterType.RARITY, rarity,
                        when (clickType) {
                            ClickType.RIGHT -> EnchantFilter.FilterStatement.OFF
                            else -> EnchantFilter.FilterStatement.ON
                        }
                    )
                    open(player)
                }

                ClickType.MIDDLE -> {
                    EnchantFilter.clearFilter(player, EnchantFilter.FilterType.RARITY, rarity)
                    open(player)
                }

                else -> {}
            }
        }
    }

    @MenuComponent
    private val reset = MenuFunctionBuilder {
        onClick { (_, _, _, event, _) ->
            val player = event.clicker
            EnchantFilter.clearFilter(player, EnchantFilter.FilterType.RARITY)
            open(player)
        }
    }
}