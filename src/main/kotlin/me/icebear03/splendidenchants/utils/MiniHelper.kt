package me.icebear03.splendidenchants.utils

import me.icebear03.splendidenchants.utils.MiniHelper.miniMessage
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player

object MiniHelper {
    val miniMessage: MiniMessage by lazy { MiniMessage.miniMessage() }
}

fun deserializeMini(text: String): Component {
    return miniMessage.deserialize(text)
}

fun Player.mini(text: String) {
    this.sendMessage(deserializeMini(text))
}