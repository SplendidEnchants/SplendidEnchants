package me.icebear03.splendidenchants.api;

import me.icebear03.splendidenchants.enchant.SplendidEnchant;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;

public class ItemAPI {
    public static HashMap<SplendidEnchant, Integer> getEnchants(ItemStack item) {
        HashMap<SplendidEnchant, Integer> result = new HashMap<>();
        if (item == null)
            return result;
        if (item.getItemMeta() == null)
            return result;
        ItemMeta meta = item.getItemMeta();
        for (Enchantment enchant : meta.getEnchants().keySet()) {
            result.put(EnchantAPI.getSplendidEnchant(enchant.getKey().getKey()), meta.getEnchantLevel(enchant));
        }
        return result;
    }

    public static boolean containsEnchant(ItemStack item, Enchantment enchant) {
        if (item == null)
            return false;
        if (item.getItemMeta() == null)
            return false;
        ItemMeta meta = item.getItemMeta();
        return meta.getEnchantLevel(enchant) > 0;
    }
}
