package me.icebear03.splendidenchants.api

import me.icebear03.splendidenchants.enchant.SplendidEnchant
import org.bukkit.enchantments.Enchantment

object EnchantAPI {

    fun getSplendidEnchant(idOrName: String): SplendidEnchant {
        TODO("不支持带颜色的 name")
    }

    fun getSplendidEnchant(enchant: Enchantment): SplendidEnchant {
        return getSplendidEnchant(enchant.key.key)
    }

    fun getName(enchant: Enchantment): String {
        return getSplendidEnchant(enchant).basicData.name
    }
}