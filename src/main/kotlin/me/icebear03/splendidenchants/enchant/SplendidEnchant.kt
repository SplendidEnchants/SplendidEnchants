package me.icebear03.splendidenchants.enchant

import me.icebear03.splendidenchants.enchant.data.BasicData
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.enchantments.EnchantmentTarget
import org.bukkit.inventory.ItemStack

/**
 * SplendidEnchants
 * me.icebear03.splendidenchants.enchant.SplendidEnchant
 *
 * @author HamsterYDS
 * @since 2023/6/19 10:02 PM
 */
class SplendidEnchant(val key: NamespacedKey) : Enchantment(key) {

    lateinit var basicData: BasicData // Please initialize

    override fun getName(): String = basicData.id

    override fun getMaxLevel(): Int = basicData.maxLevel

    override fun getStartLevel(): Int = 1

    override fun getItemTarget(): EnchantmentTarget = EnchantmentTarget.ALL

    override fun isTreasure(): Boolean {
        TODO("Not yet implemented Optional")
    }

    override fun isCursed(): Boolean {
        TODO("Not yet implemented Optional")
    }

    override fun conflictsWith(p0: Enchantment): Boolean {
        TODO("Not yet implemented")
    }

    override fun canEnchantItem(p0: ItemStack): Boolean {
        TODO("Not yet implemented")
    }
}