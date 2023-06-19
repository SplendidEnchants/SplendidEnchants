package me.icebear03.splendidenchants.enchant

import me.icebear03.splendidenchants.enchant.data.BasicData
import me.icebear03.splendidenchants.enchant.data.Rarity
import me.icebear03.splendidenchants.enchant.data.Target
import me.icebear03.splendidenchants.enchant.data.limitation.Limitations
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.enchantments.EnchantmentTarget
import org.bukkit.inventory.ItemStack

class SplendidEnchant(namespacedKey: NamespacedKey) : Enchantment(namespacedKey) {
    var basicData: BasicData? = null
    var limitations: Limitations? = null
    var rarity: Rarity? = null
    var target: Target? = null
    override fun getName(): String {
        return basicData!!.id
    }

    override fun getMaxLevel(): Int {
        return basicData!!.maxLevel
    }

    override fun getStartLevel(): Int {
        return 1
    }

    override fun getItemTarget(): EnchantmentTarget {
        return EnchantmentTarget.ALL
    }

    override fun isTreasure(): Boolean {
        return false //TODO 可选数据
    }

    override fun isCursed(): Boolean {
        return false //TODO 可选数据
    }

    override fun conflictsWith(enchantment: Enchantment): Boolean {
        return false //TODO
    }

    override fun canEnchantItem(itemStack: ItemStack): Boolean {
        return true //TODO
    }
}
