package world.icebear03.splendidenchants.ui

import org.bukkit.entity.Player
import org.serverct.parrot.parrotx.mechanism.Reloadable
import org.serverct.parrot.parrotx.ui.MenuComponent
import org.serverct.parrot.parrotx.ui.config.MenuConfiguration
import org.serverct.parrot.parrotx.ui.feature.util.MenuFunctionBuilder
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import world.icebear03.splendidenchants.api.internal.colorify
import world.icebear03.splendidenchants.api.load
import world.icebear03.splendidenchants.ui.internal.UIType
import world.icebear03.splendidenchants.ui.internal.record

@MenuComponent("Menu")
object MainMenuUI {

    @Config("gui/menu.yml")
    private lateinit var source: Configuration
    private lateinit var config: MenuConfiguration

    @Reloadable
    fun reload() {
        source.reload()
        config = MenuConfiguration(source)
    }

    fun open(player: Player) {
        player.record(UIType.MAIN_MENU)
        player.openMenu<Basic>(config.title().colorify()) {
            val (shape, templates) = config
            rows(shape.rows)
            map(*shape.array)

            load(shape, templates, player)
        }
    }

    @MenuComponent
    private val enchant_search = MenuFunctionBuilder { onClick { (_, _, _, event, _) -> EnchantSearchUI.open(event.clicker) } }

    @MenuComponent
    private val item_check = MenuFunctionBuilder { onClick { (_, _, _, event, _) -> ItemCheckUI.open(event.clicker, null, ItemCheckUI.CheckMode.LOAD) } }

    @MenuComponent
    private val anvil = MenuFunctionBuilder { onClick { (_, _, _, event, _) -> AnvilUI.open(event.clicker) } }
}