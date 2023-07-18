package world.icebear03.splendidenchants

import org.bukkit.entity.Player
import taboolib.platform.compat.PlaceholderExpansion

object PapiExpansion : PlaceholderExpansion {

    override val identifier: String
        get() = "splendidenchants"

    override fun onPlaceholderRequest(player: Player?, args: String): String {
        return args
    }
}