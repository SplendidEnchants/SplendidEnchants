package me.icebear03.splendidenchants.enchant.data

import org.bukkit.NamespacedKey

/**
 * SplendidEnchants
 * me.icebear03.splendidenchants.enchant.data.BasicData
 *
 * @author mical
 * @since 2023/6/19 7:18 PM
 */
data class BasicData(val id: String, val name: String, val maxLevel: Int) {

    val key: NamespacedKey = NamespacedKey.fromString(id, null) ?: error("minecraft")
}