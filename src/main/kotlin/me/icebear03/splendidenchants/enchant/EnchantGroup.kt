package me.icebear03.splendidenchants.enchant

import me.icebear03.splendidenchants.api.EnchantAPI
import org.bukkit.enchantments.Enchantment
import java.util.concurrent.ConcurrentHashMap

data class EnchantGroup(
    val name: String,
    val ids: List<String>
) {

    companion object {

        val groups = ConcurrentHashMap<String, EnchantGroup>()

        fun initialize() {
            TODO()
        }

        fun isIn(enchant: Enchantment, group: String): Boolean {
            return groups[group]?.ids?.contains(EnchantAPI.getName(enchant)) == true
        }
    }
}