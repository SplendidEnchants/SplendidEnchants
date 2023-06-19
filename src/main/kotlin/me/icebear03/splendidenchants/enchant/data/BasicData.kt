package me.icebear03.splendidenchants.enchant.data

import org.bukkit.NamespacedKey

data class BasicData(val id: String,
                     val name: String,
                     val maxLevel: Int) {
    val key: NamespacedKey = NamespacedKey.fromString(id, null) ?: error("minecraft")
}