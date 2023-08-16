package world.icebear03.splendidenchants.ui

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import org.serverct.parrot.parrotx.function.variable
import org.serverct.parrot.parrotx.mechanism.Reloadable
import org.serverct.parrot.parrotx.ui.MenuComponent
import org.serverct.parrot.parrotx.ui.config.MenuConfiguration
import org.serverct.parrot.parrotx.ui.feature.util.MenuFunctionBuilder
import taboolib.module.chat.colored
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import taboolib.platform.util.modifyMeta
import world.icebear03.splendidenchants.api.*
import world.icebear03.splendidenchants.enchant.EnchantFilter
import world.icebear03.splendidenchants.enchant.data.Rarity
import world.icebear03.splendidenchants.enchant.data.rarities
import world.icebear03.splendidenchants.enchant.data.rarity


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
        player.openMenu<Linked<Rarity>>(config.title().colored()) {
            val (shape, templates) = config
            rows(shape.rows)
            val slots = shape["FilterRarity:filter"].toList()
            slots(slots)
            elements { rarities.values.toList() }

            initialize(shape, templates, "FilterRarity:filter", "Previous", "Next")
            pages(shape, templates)

            val template = templates.require("FilterRarity:filter")
            onGenerate { _, element, index, slot ->
                template(slot, index) {
                    this["rarity"] = element
                    this["player"] = player
                }
            }
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

            icon.modifyMeta<ItemMeta> { this["rarity", PersistentDataType.STRING] = rarity.id }
                .variable("name", listOf(rarity.name))
                .skull(rarity.skull)
        }

        onClick { (_, _, _, event, _) ->
            val clickType = event.clickEvent().click
            val player = event.clicker
            val item = event.currentItem ?: return@onClick
            val rarity = rarity(item.itemMeta["rarity", PersistentDataType.STRING]) ?: return@onClick

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

    @MenuComponent
    private val back = MenuFunctionBuilder { onClick { (_, _, _, event, _) -> EnchantSearchUI.open(event.clicker) } }
}