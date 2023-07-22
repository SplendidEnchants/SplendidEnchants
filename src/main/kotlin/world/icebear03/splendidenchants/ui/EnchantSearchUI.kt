@file:Suppress("UNCHECKED_CAST")

package world.icebear03.splendidenchants.ui

import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.serverct.parrot.parrotx.function.variable
import org.serverct.parrot.parrotx.mechanism.Reloadable
import org.serverct.parrot.parrotx.ui.MenuComponent
import org.serverct.parrot.parrotx.ui.config.MenuConfiguration
import org.serverct.parrot.parrotx.ui.feature.util.MenuFunctionBuilder
import taboolib.common.util.replaceWithOrder
import taboolib.module.chat.colored
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import taboolib.platform.util.modifyLore
import taboolib.platform.util.nextChat
import world.icebear03.splendidenchants.api.ItemAPI
import world.icebear03.splendidenchants.enchant.SplendidEnchant
import world.icebear03.splendidenchants.util.EnchantFilter
import world.icebear03.splendidenchants.util.YamlUpdater

@MenuComponent("EnchantSearch")
object EnchantSearchUI {

    init {
        YamlUpdater.loadAndUpdate("gui/enchant_search.yml")
    }

    @Config("gui/enchant_search.yml")
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
        player.openMenu<Linked<SplendidEnchant>>(config.title().colored()) {
            virtualize()
            val (shape, templates) = config
            rows(shape.rows)
            val slots = shape["EnchantSearch\$enchant"].toList()
            slots(slots)
            elements {
                EnchantFilter.filter(player)
            }

            onBuild { _, inventory ->
                shape.all(
                    "EnchantSearch\$enchant", "Previous", "Next",
                    "EnchantSearch\$filter_rarity", "EnchantSearch\$filter_target",
                    "EnchantSearch\$filter_type", "EnchantSearch\$filter_string"
                ) { slot, index, item, _ ->
                    inventory.setItem(slot, item(slot, index))
                }
            }

            val template_enchant = templates.require("EnchantSearch\$enchant")
            onGenerate { _, member, index, slot ->
                template_enchant(slot, index, member)
            }

            onClick { event, member ->
                template_enchant.handle(event, member)
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

            val tmp = listOf("rarity", "target", "type", "string")
            tmp.forEach {
                shape["EnchantSearch\$filter_$it"].first().let { slot ->
                    set(
                        slot, templates(
                            "EnchantSearch\$filter_$it", slot, 0, false, "Fallback",
                            EnchantFilter.generateLore(EnchantFilter.FilterType.valueOf(it.uppercase()), player)
                        )
                    )
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

    @MenuComponent
    private val enchant = MenuFunctionBuilder {
        onBuild { (_, _, _, _, icon, args) ->
            val enchant = args[0] as SplendidEnchant
            val replaceMap = enchant.displayer.getReplaceMap(enchant.maxLevel)
            icon.modifyLore {
                this.replaceAll {
                    it.replaceWithOrder(*replaceMap.toArray())
                }
            }
            val name = icon.itemMeta.displayName
            ItemAPI.setName(icon, name.replaceWithOrder(*replaceMap.toArray()))
            ItemAPI.setSkull(icon, enchant.rarity.skull)

            icon
        }
    }

    @MenuComponent
    private val filter_rarity = MenuFunctionBuilder {
        onBuild { (_, _, _, _, icon, args) ->
            icon.variable("rarities", args[0] as List<String>)
        }

        onClick { (_, _, event, _) ->
            val clickType = event.virtualEvent().clickType
            val player = event.clicker
            if (clickType == ClickType.MIDDLE) {
                player.closeInventory()
                EnchantFilter.clearFilter(player, EnchantFilter.FilterType.RARITY)
                open(player)
            } else {
                FilterRarityUI.open(event.clicker)
            }
        }
    }

    @MenuComponent
    private val filter_target = MenuFunctionBuilder {
        onBuild { (_, _, _, _, icon, args) ->
            icon.variable("targets", args[0] as List<String>)
        }
    }

    @MenuComponent
    private val filter_type = MenuFunctionBuilder {
        onBuild { (_, _, _, _, icon, args) ->
            icon.variable("types", args[0] as List<String>)
        }
    }

    @MenuComponent
    private val filter_string = MenuFunctionBuilder {
        onBuild { (_, _, _, _, icon, args) ->
            icon.variable("strings", args[0] as List<String>)
        }
        onClick { (_, _, event, _) ->
            val clickType = event.virtualEvent().clickType
            val player = event.clicker
            if (clickType == ClickType.MIDDLE) {
                EnchantFilter.clearFilter(player, EnchantFilter.FilterType.STRING)
                open(player)
            }
            when (clickType) {
                ClickType.LEFT, ClickType.RIGHT -> {
                    //TODO 自定义语言
                    player.closeInventory()
                    player.sendMessage("§e请在聊天栏中输入字段...")
                    player.nextChat {
                        player.sendMessage("§a输入完成")
                        EnchantFilter.addFilter(
                            player,
                            EnchantFilter.FilterType.STRING,
                            it,
                            when (clickType) {
                                ClickType.LEFT -> EnchantFilter.FilterStatement.ON
                                ClickType.RIGHT -> EnchantFilter.FilterStatement.OFF
                                else -> EnchantFilter.FilterStatement.ON
                            }
                        )
                        open(player)
                    }
                }

                else -> {}
            }
        }
    }

    @MenuComponent
    private val back = MenuFunctionBuilder {
        onClick { (_, _, event, _) ->
            MainMenuUI.open(event.clicker)
        }
    }
}