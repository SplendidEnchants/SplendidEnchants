package world.icebear03.splendidenchants.gui

import org.serverct.parrot.parrotx.mechanism.Reloadable
import org.serverct.parrot.parrotx.ui.MenuComponent
import org.serverct.parrot.parrotx.ui.config.MenuConfiguration
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration

@MenuComponent("Menu")
object ChildListUI {

    @Config("gui/main.yml")
    private lateinit var source: Configuration
    private lateinit var config: MenuConfiguration

    @Reloadable
    fun reload() {
        source.reload()
        config = MenuConfiguration(source)
    }
}