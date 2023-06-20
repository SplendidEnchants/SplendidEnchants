package me.icebear03.splendidenchants.enchant;

import me.icebear03.splendidenchants.api.EnchantAPI;
import org.bukkit.enchantments.Enchantment;

import java.util.HashMap;
import java.util.List;

public class EnchantGroup {
    public static HashMap<String, EnchantGroup> groups = new HashMap<>();
    public String name;
    public List<String> ids;

    public static void initialize() {
        //TODO INIT FROM CONFIG
    }

    public static boolean isIn(Enchantment enchant, String group) {
        return groups.get(group).ids.contains(EnchantAPI.getName(enchant));
    }
}
