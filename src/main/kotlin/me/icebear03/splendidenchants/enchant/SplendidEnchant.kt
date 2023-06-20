package me.icebear03.splendidenchants.enchant

import io.papermc.paper.enchantments.EnchantmentRarity
import me.icebear03.splendidenchants.enchant.data.AlternativeData
import me.icebear03.splendidenchants.enchant.data.BasicData
import me.icebear03.splendidenchants.enchant.data.Rarity
import me.icebear03.splendidenchants.enchant.data.Target
import me.icebear03.splendidenchants.enchant.data.limitation.Limitations
import net.kyori.adventure.text.Component
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.enchantments.EnchantmentTarget
import org.bukkit.entity.EntityCategory
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

class SplendidEnchant(namespacedKey: NamespacedKey) : Enchantment(namespacedKey) {

    lateinit var basicData: BasicData
    lateinit var alternativeData: AlternativeData
    lateinit var rarity: Rarity
    lateinit var target: Target
    lateinit var limitations: Limitations

    override fun translationKey(): String = basicData.id

    override fun getName(): String = basicData.id

    override fun getMaxLevel(): Int = basicData.maxLevel

    override fun getStartLevel(): Int = 1

    override fun getItemTarget(): EnchantmentTarget = EnchantmentTarget.ALL

    override fun isTreasure(): Boolean = alternativeData.isTreasure

    override fun isCursed(): Boolean = alternativeData.isCursed

    override fun conflictsWith(enchant: Enchantment): Boolean = limitations.conflictWith(enchant)

    override fun canEnchantItem(item: ItemStack): Boolean = limitations.checkAvailable(item).first

    override fun displayName(level: Int): Component {
        TODO("Displayer")
    }

    override fun isTradeable(): Boolean = alternativeData.isTradeable

    override fun isDiscoverable(): Boolean = alternativeData.isDiscoverable

    override fun getRarity(): EnchantmentRarity = EnchantmentRarity.COMMON

    override fun getDamageIncrease(level: Int, entityCategory: EntityCategory): Float = 0.0f

    override fun getActiveSlots(): MutableSet<EquipmentSlot> = hashSetOf()
}