package world.icebear03.splendidenchants.api

import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import world.icebear03.splendidenchants.enchant.SplendidEnchant

object ItemAPI {
    fun setLore(item: ItemStack?, lore: List<String>): ItemStack? {
        if (item == null) return null
        if (item.itemMeta == null) return item
        val clone = item.clone()
        val meta = clone.itemMeta!!
        meta.lore = lore
        clone.itemMeta = meta
        return clone
    }

    fun getEnchants(item: ItemStack?): HashMap<SplendidEnchant, Int> {
        val result = hashMapOf<SplendidEnchant, Int>()
        if (item == null) return result
        if (item.itemMeta == null) return result
        val meta = item.itemMeta!!
        if (meta is EnchantmentStorageMeta) {
            meta.storedEnchants.keys.forEach {
                result[EnchantAPI.getSplendidEnchant(it)] = meta.storedEnchants[it]!!
            }
        } else {
            meta.enchants.keys.forEach {
                result[EnchantAPI.getSplendidEnchant(it)] = meta.getEnchantLevel(it)
            }
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
        if (meta is EnchantmentStorageMeta) {
            if (meta.storedEnchants[enchant] != null)
                return meta.storedEnchants[enchant]!!
        } else {
            if (meta.hasEnchant(enchant))
                return meta.getEnchantLevel(enchant)
        }
        return 0
    }

    fun isBook(item: ItemStack): Boolean {
        val meta = item.itemMeta
        return meta is EnchantmentStorageMeta
    }

    //注意原meta会更改
    fun addEnchant(meta: ItemMeta, enchant: SplendidEnchant, level: Int): ItemMeta {
        if (meta is EnchantmentStorageMeta) {
            meta.storedEnchants[enchant] = level
        } else {
            meta.enchants[enchant] = level
        }
        return meta
    }

    fun addEnchant(item: ItemStack, enchant: SplendidEnchant, level: Int): ItemStack {
        if (item.itemMeta == null) return item
        val clone = item.clone()
        val meta = addEnchant(clone.itemMeta, enchant, level)
        clone.itemMeta = meta
        return clone
    }

    //注意原meta会更改
    fun setEnchants(meta: ItemMeta, enchants: Map<Enchantment, Int>): ItemMeta {
        if (meta is EnchantmentStorageMeta) {
            meta.storedEnchants.clear()
            meta.storedEnchants.putAll(enchants)
        } else {
            meta.enchants.clear()
            meta.enchants.putAll(enchants)
        }
        return meta
    }

    fun setEnchants(item: ItemStack, enchants: Map<Enchantment, Int>): ItemStack {
        if (item.itemMeta == null) return item
        val clone = item.clone()
        val meta = setEnchants(clone.itemMeta, enchants)
        clone.itemMeta = meta
        return clone
    }
}