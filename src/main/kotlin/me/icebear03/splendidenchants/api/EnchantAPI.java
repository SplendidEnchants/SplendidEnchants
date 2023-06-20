package me.icebear03.splendidenchants.api;

import me.icebear03.splendidenchants.enchant.SplendidEnchant;
import org.bukkit.enchantments.Enchantment;

public class EnchantAPI {
    public static SplendidEnchant getSplendidEnchant(String idOrName) {
        return null;
        //TODO 不支持带颜色的name
    }

    public static SplendidEnchant getSplendidEnchant(Enchantment enchant) {
        return getSplendidEnchant(enchant.getKey().getKey());
    }

    public static String getName(Enchantment enchant) {
        return getSplendidEnchant(enchant).basicData.getName();
    }
}
