@file:Suppress("deprecation")

package world.icebear03.splendidenchants.api

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
import org.serverct.parrot.parrotx.function.textured
import taboolib.platform.util.giveItem
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

    fun createBook(enchants: Map<Enchantment, Int>): ItemStack {
        return ItemStack(Material.ENCHANTED_BOOK).modifyMeta<ItemMeta> {
            setEnchants(this, enchants)
        }
    }

    fun giveBook(player: Player, enchant: SplendidEnchant, level: Int = enchant.maxLevel) {
        player.giveItem(createBook(mapOf(enchant to level)))
    }

    fun containsEnchant(item: ItemStack?, enchant: Enchantment): Boolean {
        return (item?.itemMeta?.getEnchantLevel(enchant) ?: 0) > 0
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

    fun isBook(item: ItemStack): Boolean {
        return item.itemMeta is EnchantmentStorageMeta
    }

    //注意原meta会更改
    fun addEnchant(meta: ItemMeta, enchant: SplendidEnchant, level: Int): ItemMeta {
        if (meta is EnchantmentStorageMeta) {
            meta.addStoredEnchant(enchant, level, true)
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
            enchants.forEach { meta.addStoredEnchant(it.key, it.value, true); }
        } else {
            enchants.forEach { meta.addEnchant(it.key, it.value, true); }
        }
        return meta
    }

    fun setEnchants(item: ItemStack, enchants: Map<Enchantment, Int>): ItemStack {
        return if (item.itemMeta == null) item else item.clone().modifyMeta<ItemMeta> {
            setEnchants(this, enchants)
        }
    }

    fun clearEnchants(item: ItemStack): ItemStack {
        return if (item.itemMeta == null) item else item.clone().modifyMeta<ItemMeta> {
            clearEnchants(this)
        }
    }

    fun clearEnchants(meta: ItemMeta): ItemMeta {
        if (meta is EnchantmentStorageMeta) {
            getEnchants(meta).forEach {
                meta.removeStoredEnchant(it.key)
            }
        } else {
            getEnchants(meta).forEach {
                meta.removeEnchant(it.key)
            }
        }
        return meta
    }

    fun getName(item: ItemStack?): String? {
        return item?.itemMeta?.displayName
    }

    fun setName(item: ItemStack?, name: String?): ItemStack? {
        return item?.modifyMeta<ItemMeta> {
            setDisplayName(name)
        }
    }

    fun getDamage(item: ItemStack?): Int? {
        return (item?.itemMeta as? Damageable)?.damage
    }

    fun setDamage(item: ItemStack?, damage: Int?): ItemStack? {
        return item?.modifyMeta<Damageable> {
            this.damage = damage ?: return@modifyMeta
        }
    }

    fun removeEnchant(item: ItemStack, enchant: SplendidEnchant): ItemStack {
        return if (item.itemMeta == null) item else item.modifyMeta<ItemMeta> {
            removeEnchant(this, enchant)
        }
    }

    fun removeEnchant(meta: ItemMeta, enchant: SplendidEnchant): ItemMeta {
        if (meta is EnchantmentStorageMeta) {
            meta.removeStoredEnchant(enchant)
        } else {
            meta.removeEnchant(enchant)
        }
        return meta
    }

    fun setSkull(item: ItemStack, skull: String): ItemStack {
        if (item.itemMeta is SkullMeta)
            return item.textured(skull)
        return item
    }
}

val ItemStack.fixedEnchants
    get(): Map<SplendidEnchant, Int> {
        return itemMeta?.let { meta ->
            (if (meta is EnchantmentStorageMeta) meta.storedEnchants
            else meta.enchants).mapKeys { (key, _) -> EnchantAPI.getSplendidEnchant(key) }
        } ?: emptyMap()
    }