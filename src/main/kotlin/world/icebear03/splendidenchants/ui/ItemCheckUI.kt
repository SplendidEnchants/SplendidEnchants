@file:Suppress("UNCHECKED_CAST")

package world.icebear03.splendidenchants.ui

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.serverct.parrot.parrotx.function.variables
import org.serverct.parrot.parrotx.mechanism.Reloadable
import org.serverct.parrot.parrotx.ui.MenuComponent
import org.serverct.parrot.parrotx.ui.config.MenuConfiguration
import org.serverct.parrot.parrotx.ui.feature.util.MenuFunctionBuilder
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import world.icebear03.splendidenchants.api.*
import world.icebear03.splendidenchants.api.internal.colorify
import world.icebear03.splendidenchants.enchant.SplendidEnchant
import world.icebear03.splendidenchants.ui.internal.UIType
import world.icebear03.splendidenchants.ui.internal.record

@MenuComponent("ItemCheck")
object ItemCheckUI {

    @Config("gui/item_check.yml")
    private lateinit var source: Configuration
    private lateinit var config: MenuConfiguration

    @Reloadable
    fun reload() {
        source.reload()
        config = MenuConfiguration(source)
    }

    fun open(player: Player, item: ItemStack? = null) {
        player.record(UIType.ITEM_CHECK, "item" to item)
        player.openMenu<Linked<Pair<SplendidEnchant, Int>>>(config.title().colorify()) {
            val (shape, templates) = config
            rows(shape.rows)
            val slots = shape["ItemCheck:enchant"].toList()
            slots(slots)
            elements { item.fixedEnchants.toList() }

            load(shape, templates, false, player, "ItemCheck:enchant", "ItemCheck:item", "Previous", "Next")
            pages(shape, templates)

            val template = templates.require("ItemCheck:enchant")
            onGenerate { _, element, index, slot ->
                template(slot, index) {
                    this["enchantPair"] = element
                    this["player"] = player
                    this["item"] = item
                }
            }

            item?.let { setSlots(shape, templates, "ItemCheck:item", listOf(), "item" to it) }

            onClick { event ->
                event.isCancelled = true
                if (event.rawSlot in shape)
                    templates[event.rawSlot]?.handle(this, event)
                if (event.rawSlot !in shape && event.currentItem?.type != Material.AIR)
                    open(player, event.currentItem)
            }
        }
    }

    @MenuComponent
    private val enchant = MenuFunctionBuilder {
        onBuild { (_, _, _, _, icon, args) ->
            val enchantPair = args["enchantPair"] as Pair<SplendidEnchant, Int>
            val enchant = enchantPair.first
            val level = enchantPair.second
            val holders = enchant.displayer.holders(level, args["player"] as Player, args["item"] as ItemStack)
            icon.variables { variable -> listOf(holders[variable] ?: "") }
                .skull(enchant.rarity.skull)
        }
    }

    @MenuComponent
    private val item = MenuFunctionBuilder {
        onBuild { (_, _, _, _, icon, args) -> args["item"] as? ItemStack ?: icon }
        onClick { (_, _, _, event, _) -> open(event.clicker, null) }
    }
}