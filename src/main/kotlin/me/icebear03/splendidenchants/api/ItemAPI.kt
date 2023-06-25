package me.icebear03.splendidenchants.api

import me.icebear03.splendidenchants.enchant.SplendidEnchant
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

object ItemAPI {

    fun getEnchants(item: ItemStack?): HashMap<SplendidEnchant, Int> {
        val result = hashMapOf<SplendidEnchant, Int>()
        if (item == null) return result
        if (item.itemMeta == null) return result
        val meta = item.itemMeta!!
        meta.enchants.keys.forEach { enchant ->
            result[EnchantAPI.getSplendidEnchant(enchant)] = meta.getEnchantLevel(enchant)
        }
        return result
    }

    fun containsEnchant(item: ItemStack?, enchant: Enchantment): Boolean {
        if (item == null) return false
        if (item.itemMeta == null) return false
        val meta = item.itemMeta!!
        return meta.getEnchantLevel(enchant) > 0
    }

    fun <T, Z> getItemData(item: ItemStack?, dataKey: String, type: PersistentDataType<T, Z>): Z? {
        if (item == null) return null
        if (item.itemMeta == null) return null
        val meta = item.itemMeta!!
        val pdc = meta.persistentDataContainer
        val key = NamespacedKey.fromString("splendidenchant_$dataKey")!!
        if (!pdc.has(key, type))
            return null
        return pdc.get(key, type)
    }

    fun getLevel(item: ItemStack?, enchant: Enchantment): Int {
        if (item == null) return 0
        if (item.itemMeta == null) return 0
        val meta = item.itemMeta!!
        if (meta.hasEnchant(enchant)) return meta.getEnchantLevel(enchant)
        return 0
    }

    fun getLore(item: ItemStack?): MutableList<String> {
        if (item == null) return mutableListOf()
        if (item.itemMeta == null) return mutableListOf()
        val meta = item.itemMeta!!
        //TODO 注意这里不符合advanture api
        if (meta.lore == null) return mutableListOf()
        return meta.lore!!
    }

    fun setLore(item: ItemStack, lore: List<String>): ItemStack {
        val meta = item.itemMeta
        meta.lore = lore
        item.itemMeta = meta
        return item
    }
}