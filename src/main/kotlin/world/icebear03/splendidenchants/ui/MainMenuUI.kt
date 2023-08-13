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
import taboolib.module.ui.type.Basic
import world.icebear03.splendidenchants.api.internal.YamlUpdater

@MenuComponent("Menu")
object MainMenuUI {

    init {
        YamlUpdater.loadAndUpdate("gui/menu.yml")
    }

    @Config("gui/menu.yml")
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
        player.openMenu<Basic>(config.title().colored()) {
            virtualize()
            val (shape, templates) = config
            rows(shape.rows)
            map(*shape.array)

            onBuild { _, inventory ->
                shape.all { slot, index, item, _ ->
                    inventory.setItem(slot, item(slot, index))
                }
            }

            onClick {
                it.isCancelled = true
                if (it.rawSlot in shape) {
                    templates[it.rawSlot]?.handle(it)
                }
            }
        }
    }

    @MenuComponent
    private val enchant_search = MenuFunctionBuilder {
        onClick { (_, _, event, _) ->
            EnchantSearchUI.open(event.clicker)
        }
    }

    @MenuComponent
    private val item_check = MenuFunctionBuilder {
        onClick { (_, _, event, _) ->
            ItemCheckUI.open(event.clicker)
        }
    }

    @MenuComponent
    private val anvil = MenuFunctionBuilder {
        onClick { (_, _, event, _) ->
            AnvilUI.open(event.clicker)
        }
    }

    @MenuComponent
    private val grindstone = MenuFunctionBuilder {
        onClick { (_, _, _, _) ->
            TODO("open")
        }
    }
}