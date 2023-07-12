package world.icebear03.splendidenchants

import org.bukkit.entity.Player
import taboolib.platform.compat.PlaceholderExpansion

class PapiExpansion(override val identifier: String) : PlaceholderExpansion {
    override fun onPlaceholderRequest(player: Player?, holder: String): String {
        if (player == null)
            return holder
        return holder
    }
}