package world.icebear03.splendidenchants.api

import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import taboolib.platform.util.modifyLore
import taboolib.platform.util.modifyMeta
import world.icebear03.splendidenchants.enchant.SplendidEnchant

object ItemAPI {

    fun setLore(item: ItemStack?, lore: List<String>): ItemStack? {
        return if (item?.itemMeta == null) null else item.clone().modifyLore {
            clear()
            addAll(lore)
        }
    }

    fun getEnchants(item: ItemStack?): Map<SplendidEnchant, Int> {
        return item?.itemMeta?.let { meta ->
            if (meta is EnchantmentStorageMeta) {
                meta.storedEnchants
            } else {
                meta.enchants
            }.mapKeys { (key, _) -> EnchantAPI.getSplendidEnchant(key) }
        } ?: emptyMap()
    }

    fun containsEnchant(item: ItemStack?, enchant: Enchantment): Boolean {
        return (item?.itemMeta?.getEnchantLevel(enchant) ?: 0) > 0
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
        return item?.itemMeta?.let { meta ->
            if (meta is EnchantmentStorageMeta) {
                meta.storedEnchants[enchant]
            } else {
                meta.getEnchantLevel(enchant)
            }
        } ?: 0
    }

    // FIXME: 这个方法有什么必要么?
    fun isBook(item: ItemStack): Boolean {
        return item.itemMeta is EnchantmentStorageMeta
    }

    //注意原meta会更改
    //TODO 存在问题： 无法附魔超出最大级，无论是true还是false
    fun addEnchant(meta: ItemMeta, enchant: SplendidEnchant, level: Int): ItemMeta {
        if (meta is EnchantmentStorageMeta) {
            meta.addEnchant(enchant, level, true)
        } else {
            meta.addEnchant(enchant, level, true)
        }
        return meta
    }

    fun addEnchant(item: ItemStack, enchant: SplendidEnchant, level: Int): ItemStack {
        return if (item.itemMeta == null) item else item.modifyMeta<ItemMeta> {
            addEnchant(this, enchant, level)
        }
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
        return if (item.itemMeta == null) item else item.clone().modifyMeta<ItemMeta> {
            setEnchants(this, enchants)
        }
    }

    fun getName(item: ItemStack?): String? {
        if (item == null) return null
        if (item.itemMeta == null) return null
        val meta = item.itemMeta!!
        return meta.displayName
    }

    fun setName(item: ItemStack?, name: String?): ItemStack? {
        if (item == null) return null
        if (item.itemMeta == null) return null
        val meta = item.itemMeta!!
        meta.setDisplayName(name)
        item.setItemMeta(meta)
        return item
    }

    fun getDamage(item: ItemStack?): Int? {
        if (item == null) return null
        if (item.itemMeta == null) return null
        val meta = item.itemMeta!!
        if (meta is Damageable)
            return meta.damage
        return null
    }

    fun setDamage(item: ItemStack?, damage: Int?): ItemStack? {
        if (item == null) return null
        if (item.itemMeta == null) return item
        if (damage == null) return item
        val meta = item.itemMeta!!
        if (meta is Damageable) {
            meta.damage = damage
        }
        return item
    }
}