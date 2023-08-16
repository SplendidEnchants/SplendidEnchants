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
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import taboolib.platform.util.modifyMeta
import world.icebear03.splendidenchants.api.*
import world.icebear03.splendidenchants.api.internal.colorify
import world.icebear03.splendidenchants.enchant.EnchantFilter
import world.icebear03.splendidenchants.enchant.data.Group
import world.icebear03.splendidenchants.enchant.data.group
import world.icebear03.splendidenchants.enchant.data.groups


@MenuComponent("FilterGroup")
object FilterGroupUI {

    @Config("gui/filter_group.yml")
    private lateinit var source: Configuration
    private lateinit var config: MenuConfiguration

    @Reloadable
    fun reload() {
        source.reload()
        config = MenuConfiguration(source)
    }

    fun open(player: Player) {
        player.openMenu<Linked<Group>>(config.title().colorify()) {
            val (shape, templates) = config
            rows(shape.rows)
            val slots = shape["FilterGroup:filter"].toList()
            slots(slots)
            elements { groups.values.toList() }

            load(shape, templates, "FilterGroup:filter", "Previous", "Next")
            pages(shape, templates)

            val template = templates.require("FilterGroup:filter")
            onGenerate { _, element, index, slot ->
                template(slot, index) {
                    this["group"] = element
                    this["player"] = player
                }
            }
        }
    }

    @MenuComponent
    private val filter = MenuFunctionBuilder {
        onBuild { (_, _, _, _, icon, args) ->
            val group = args["group"] as Group
            val player = args["player"] as Player

            when (EnchantFilter.getStatement(player, EnchantFilter.FilterType.GROUP, group)) {
                EnchantFilter.FilterStatement.ON -> icon.type = Material.LIME_STAINED_GLASS_PANE
                EnchantFilter.FilterStatement.OFF -> icon.type = Material.RED_STAINED_GLASS_PANE
                else -> {}
            }

            icon.modifyMeta<ItemMeta> { this["group", PersistentDataType.STRING] = group.name }
                .variable("name", listOf(group.name))
                .skull(group.skull)
        }

        onClick { (_, _, _, event, _) ->
            val clickType = event.clickEvent().click
            val player = event.clicker
            val item = event.currentItem ?: return@onClick
            val group = group(item.itemMeta["group", PersistentDataType.STRING]) ?: return@onClick

            when (clickType) {
                ClickType.LEFT, ClickType.RIGHT -> {
                    EnchantFilter.clearFilter(player, EnchantFilter.FilterType.GROUP, group)
                    EnchantFilter.addFilter(
                        player, EnchantFilter.FilterType.GROUP, group,
                        when (clickType) {
                            ClickType.RIGHT -> EnchantFilter.FilterStatement.OFF
                            else -> EnchantFilter.FilterStatement.ON
                        }
                    )
                    open(player)
                }

                ClickType.MIDDLE -> {
                    EnchantFilter.clearFilter(player, EnchantFilter.FilterType.GROUP, group)
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
            EnchantFilter.clearFilter(player, EnchantFilter.FilterType.GROUP)
            open(player)
        }
    }

    @MenuComponent
    private val back = MenuFunctionBuilder { onClick { (_, _, _, event, _) -> EnchantSearchUI.open(event.clicker) } }
}