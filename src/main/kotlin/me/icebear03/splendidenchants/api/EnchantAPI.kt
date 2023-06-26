package me.icebear03.splendidenchants.api

import me.icebear03.splendidenchants.enchant.EnchantLoader
import me.icebear03.splendidenchants.enchant.SplendidEnchant
import org.bukkit.enchantments.Enchantment

object EnchantAPI {

    fun getSplendidEnchant(idOrName: String): SplendidEnchant? {
        return EnchantLoader.enchantById[idOrName] ?: EnchantLoader.enchantByName[idOrName]
    }

    fun getSplendidEnchant(enchant: Enchantment): SplendidEnchant {
        return getSplendidEnchant(enchant.key.key)!!
    }

    fun getName(enchant: Enchantment): String {
        return getSplendidEnchant(enchant).basicData.name
    }

    fun getId(enchant: Enchantment): String {
        return getSplendidEnchant(enchant).basicData.id
    }

    fun isSame(a: Enchantment, b: Enchantment): Boolean = getId(a) == getId(b)
}