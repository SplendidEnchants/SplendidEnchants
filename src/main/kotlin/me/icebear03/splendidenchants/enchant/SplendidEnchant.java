package me.icebear03.splendidenchants.enchant;

import me.icebear03.splendidenchants.enchant.data.BasicData;
import me.icebear03.splendidenchants.enchant.data.Rarity;
import me.icebear03.splendidenchants.enchant.data.Target;
import me.icebear03.splendidenchants.enchant.data.limitation.Limitations;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SplendidEnchant extends Enchantment {
    public BasicData basicData;
    public Limitations limitations;
    public Rarity rarity;
    public Target target;

    public SplendidEnchant(@NotNull NamespacedKey namespacedKey) {
        super(namespacedKey);
    }

    @NotNull
    @Override
    public String getName() {
        return basicData.getId();
    }

    @Override
    public int getMaxLevel() {
        return basicData.getMaxLevel();
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @NotNull
    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.ALL;
    }

    @Override
    public boolean isTreasure() {
        return false; //TODO 可选数据
    }

    @Override
    public boolean isCursed() {
        return false; //TODO 可选数据
    }

    @Override
    public boolean conflictsWith(@NotNull Enchantment enchantment) {
        return false; //TODO
    }

    @Override
    public boolean canEnchantItem(@NotNull ItemStack itemStack) {
        return true; //TODO
    }
}
