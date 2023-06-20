package me.icebear03.splendidenchants.api

import me.icebear03.splendidenchants.enchant.SplendidEnchant
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack

object ItemAPI {

    fun getEnchants(item: ItemStack?): HashMap<SplendidEnchant, Int> {
        val result = hashMapOf<SplendidEnchant, Int>()
        if (item == null) return result
        if (item.itemMeta == null) return result
        item.itemMeta.enchants.keys.forEach { enchant ->
            result[EnchantAPI.getSplendidEnchant(enchant.key.key)] = item.itemMeta.getEnchantLevel(enchant)
        }
        return result
    }

    fun containsEnchant(item: ItemStack?, enchant: Enchantment): Boolean {
        if (item == null) return false
        if (item.itemMeta == null) return false
        val meta = item.itemMeta
        return meta.getEnchantLevel(enchant) > 0
    }
}