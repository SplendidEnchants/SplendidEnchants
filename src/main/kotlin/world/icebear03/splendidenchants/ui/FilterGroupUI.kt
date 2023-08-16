package world.icebear03.splendidenchants.ui

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import org.serverct.parrot.parrotx.function.variables
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
import world.icebear03.splendidenchants.api.ItemAPI
import world.icebear03.splendidenchants.enchant.EnchantFilter
import world.icebear03.splendidenchants.enchant.data.Group


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
        if (!::config.isInitialized) {
            config = MenuConfiguration(source)
        }
        player.openMenu<Linked<Group>>(config.title().colored()) {
            virtualize()
            val (shape, templates) = config
            rows(shape.rows)
            val slots = shape["FilterGroup\$filter"].toList()
            slots(slots)
            elements { Group.groups.values.toList() }

            onBuild { _, inventory ->
                shape.all(
                    "Previous", "Next",
                    "FilterGroup\$filter"
                ) { slot, index, item, _ ->
                    inventory.setItem(slot, item(slot, index))
                }
            }

            val template_filter = templates.require("FilterGroup\$filter")
            onGenerate { _, member, index, slot ->
                template_filter(slot, index, member, player)
            }

            onClick { event, member ->
                template_filter.handle(event, member)
            }

            shape["Previous"].first().let { slot ->
                setPreviousPage(slot) { it, _ ->
                    templates("Previous", slot, it)
                }
            }
            shape["Next"].first().let { slot ->
                setNextPage(slot) { it, _ ->
                    templates("Next", slot, it)
                }
            }

            onClick { event ->
                event.isCancelled = true
                if (event.rawSlot in shape && event.rawSlot !in slots) {
                    templates[event.rawSlot]?.handle(event)
                }
            }
        }
    }

    val key = NamespacedKey.minecraft("group")

    @MenuComponent
    private val filter = MenuFunctionBuilder {
        onBuild { (_, _, _, _, icon, args) ->
            val group = args[0] as Group
            val player = args[1] as Player
            icon.variables {
                when (it) {
                    "name" -> listOf(group.name)
                    else -> listOf()
                }
            }
            ItemAPI.setSkull(icon, group.skull)

            val statement = EnchantFilter.getStatement(player, EnchantFilter.FilterType.GROUP, group)

            when (statement) {
                EnchantFilter.FilterStatement.ON ->
                    icon.type = Material.LIME_STAINED_GLASS_PANE

                EnchantFilter.FilterStatement.OFF ->
                    icon.type = Material.RED_STAINED_GLASS_PANE

                else -> {}
            }

            icon.modifyMeta<ItemMeta> {
                this.persistentDataContainer.set(
                    key, PersistentDataType.STRING, group.name
                )
            }

            icon
        }

        onClick { (_, _, event, _) ->
            val clickType = event.virtualEvent().clickType
            val player = event.clicker
            val item = event.virtualEvent().clickItem

            if (item.type == Material.AIR)
                return@onClick
            val group =
                Group.fromName(item.itemMeta.persistentDataContainer.get(key, PersistentDataType.STRING)!!)!!

            if (clickType == ClickType.MIDDLE) {
                EnchantFilter.clearFilter(player, EnchantFilter.FilterType.GROUP, group)
                open(player)
            }
            when (clickType) {
                ClickType.LEFT, ClickType.RIGHT -> {
                    EnchantFilter.clearFilter(player, EnchantFilter.FilterType.GROUP, group)
                    EnchantFilter.addFilter(
                        player,
                        EnchantFilter.FilterType.GROUP,
                        group,
                        when (clickType) {
                            ClickType.LEFT -> EnchantFilter.FilterStatement.ON
                            ClickType.RIGHT -> EnchantFilter.FilterStatement.OFF
                            else -> EnchantFilter.FilterStatement.ON
                        }
                    )
                    open(player)
                }

                else -> {}
            }
        }
    }

    @MenuComponent
    private val reset = MenuFunctionBuilder {
        onClick { (_, _, event, _) ->
            val player = event.clicker
            EnchantFilter.clearFilter(player, EnchantFilter.FilterType.GROUP)
            open(player)
        }
    }

    @MenuComponent
    private val back = MenuFunctionBuilder {
        onClick { (_, _, event, _) ->
            EnchantSearchUI.open(event.clicker)
        }
    }
}