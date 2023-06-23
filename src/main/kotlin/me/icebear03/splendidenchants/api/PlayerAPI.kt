package me.icebear03.splendidenchants.api

import org.bukkit.entity.Player
import taboolib.platform.compat.replacePlaceholder

object PlayerAPI {

    fun convertPlaceHolders(string: String, player: Player): String {
        return string.replacePlaceholder(player)
    }
}