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
import world.icebear03.splendidenchants.enchant.data.Target
import world.icebear03.splendidenchants.util.EnchantFilter
import world.icebear03.splendidenchants.util.YamlUpdater


@MenuComponent("FilterTarget")
object FilterTargetUI {

    init {
        YamlUpdater.loadAndUpdate("gui/filter_target.yml")
    }

    @Config("gui/filter_target.yml")
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
        player.openMenu<Linked<Target>>(config.title().colored()) {
            virtualize()
            val (shape, templates) = config
            rows(shape.rows)
            val slots = shape["FilterTarget\$filter"].toList()
            slots(slots)
            elements { Target.targets.values.filter { it.id != "unknown" }.toList() }

            onBuild { _, inventory ->
                shape.all(
                    "Previous", "Next",
                    "FilterTarget\$filter"
                ) { slot, index, item, _ ->
                    inventory.setItem(slot, item(slot, index))
                }
            }

            val template_filter = templates.require("FilterTarget\$filter")
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

    val key = NamespacedKey.minecraft("target")

    @MenuComponent
    private val filter = MenuFunctionBuilder {
        onBuild { (_, _, _, _, icon, args) ->
            val target = args[0] as Target
            val player = args[1] as Player
            icon.variables {
                when (it) {
                    "name" -> listOf(target.name)
                    else -> listOf()
                }
            }
            ItemAPI.setSkull(icon, target.skull)

            val statement = EnchantFilter.getStatement(player, EnchantFilter.FilterType.TARGET, target)

            when (statement) {
                EnchantFilter.FilterStatement.ON ->
                    icon.type = Material.LIME_STAINED_GLASS_PANE

                EnchantFilter.FilterStatement.OFF ->
                    icon.type = Material.RED_STAINED_GLASS_PANE

                else -> {}
            }

            icon.modifyMeta<ItemMeta> {
                this.persistentDataContainer.set(
                    key, PersistentDataType.STRING, target.id
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
            val target =
                Target.fromIdOrName(item.itemMeta.persistentDataContainer.get(key, PersistentDataType.STRING)!!)

            if (clickType == ClickType.MIDDLE) {
                EnchantFilter.clearFilter(player, EnchantFilter.FilterType.TARGET, target)
                open(player)
            }
            when (clickType) {
                ClickType.LEFT, ClickType.RIGHT -> {
                    EnchantFilter.clearFilter(player, EnchantFilter.FilterType.TARGET, target)
                    EnchantFilter.addFilter(
                        player,
                        EnchantFilter.FilterType.TARGET,
                        target,
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
            EnchantFilter.clearFilter(player, EnchantFilter.FilterType.TARGET)
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