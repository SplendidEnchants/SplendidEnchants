@file:Suppress("UNCHECKED_CAST")

package world.icebear03.splendidenchants.ui

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
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
import world.icebear03.splendidenchants.api.ItemAPI
import world.icebear03.splendidenchants.api.internal.YamlUpdater
import world.icebear03.splendidenchants.enchant.SplendidEnchant

@MenuComponent("ItemCheck")
object ItemCheckUI {

    init {
        YamlUpdater.loadAndUpdate("gui/item_check.yml")
    }

    @Config("gui/item_check.yml")
    private lateinit var source: Configuration
    private lateinit var config: MenuConfiguration

    @Reloadable
    fun reload() {
        source.reload()
        config = MenuConfiguration(source)
    }

    fun open(player: Player, item: ItemStack? = null) {
        if (!::config.isInitialized) {
            config = MenuConfiguration(source)
        }
        player.openMenu<Linked<Pair<SplendidEnchant, Int>>>(config.title().colored()) {
            virtualize()
            val (shape, templates) = config
            rows(shape.rows)
            val slots = shape["ItemCheck\$enchant"].toList()
            slots(slots)
            elements { ItemAPI.getEnchants(item).toList() }

            onBuild { _, inventory ->
                shape.all("ItemCheck\$enchant", "ItemCheck\$item", "Previous", "Next") { slot, index, item, _ ->
                    inventory.setItem(slot, item(slot, index))
                }
            }

            val template = templates.require("ItemCheck\$enchant")
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

            shape["ItemCheck\$item"].first().let { slot ->
                set(
                    slot,
                    templates("ItemCheck\$item", slot, 0, false, "Fallback", item)
                )
            }


            onClick { event ->
                event.isCancelled = true
                if (event.rawSlot in shape && event.rawSlot !in slots) {
                    templates[event.rawSlot]?.handle(event)
                }
                if (event.rawSlot !in shape) {
                    val clicked = event.virtualEvent().clickItem
                    if (clicked.type != Material.AIR)
                        open(player, clicked)
                }
            }
        }
    }

    @MenuComponent
    private val enchant = MenuFunctionBuilder {
        onBuild { (_, _, _, _, icon, args) ->
            val pair = args[0] as Pair<SplendidEnchant, Int>
            val enchant = pair.first
            val replaceMap = enchant.displayer.holders(pair.second)
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
    private val item = MenuFunctionBuilder {
        onBuild { (_, _, _, _, icon, args) ->
            if (args[0] == null)
                icon
            else
                args[0] as ItemStack
        }
        onClick { (_, _, event, _) ->
            open(event.clicker, null)
        }
    }

    @MenuComponent
    private val back = MenuFunctionBuilder {
        onClick { (_, _, event, _) ->
            MainMenuUI.open(event.clicker)
        }
    }
}