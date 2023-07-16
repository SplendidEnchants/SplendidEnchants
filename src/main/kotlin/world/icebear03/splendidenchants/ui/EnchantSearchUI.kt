@file:Suppress("UNCHECKED_CAST")

package world.icebear03.splendidenchants.ui

import org.bukkit.entity.Player
import org.serverct.parrot.parrotx.mechanism.Reloadable
import org.serverct.parrot.parrotx.ui.MenuComponent
import org.serverct.parrot.parrotx.ui.config.MenuConfiguration
import org.serverct.parrot.parrotx.ui.feature.util.MenuFunctionBuilder
import taboolib.module.chat.colored
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import world.icebear03.splendidenchants.api.ItemAPI
import world.icebear03.splendidenchants.enchant.SplendidEnchant
import world.icebear03.splendidenchants.ui.util.applyReplaceMap
import world.icebear03.splendidenchants.util.YamlUpdater

/**
 * SplendidEnchants
 * world.icebear03.splendidenchants.ui.EnchantSearchUI
 *
 * @author Mical
 * @since 2023/7/11 22:52
 */
@MenuComponent("EnchantSearch")
object EnchantSearchUI {

    init {
        YamlUpdater.loadAndUpdate("gui/enchant_search.yml", listOf())
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
        player.openMenu<Linked<Pair<SplendidEnchant, Int>>>(config.title().colored()) {
            virtualize()
            val (shape, templates) = config
            rows(shape.rows)
            val slots = shape["EnchantSearch\$information"].toList()
            slots(slots)
            elements { ItemAPI.getEnchants(player.equipment.itemInMainHand).toList() }

            onBuild { _, inventory ->
                shape.all("EnchantSearch\$information", "Previous", "Next") { slot, index, item, _ ->
                    inventory.setItem(slot, item(slot, index))
                }
            }

            val template = templates.require("EnchantSearch\$information")
            onGenerate { _, element, index, slot ->
                template(slot, index, element, player)
            }

            onClick { event, element ->
                template.handle(event, element)
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

    @MenuComponent
    private val information = MenuFunctionBuilder {
        onBuild { (_, _, _, _, icon, args) ->
            val (enchant, level) = args[0] as Pair<SplendidEnchant, Int>
            val player = args[1] as Player
            (icon to level) applyReplaceMap (enchant to player)
        }
    }

    @MenuComponent
    private val back = MenuFunctionBuilder {
        onClick { (_, _, event, _) ->
            MainMenuUI.open(event.clicker)
        }
    }
}