package world.icebear03.splendidenchants.api

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import taboolib.common5.RandomList
import taboolib.platform.util.modifyMeta
import world.icebear03.splendidenchants.api.internal.colorify
import world.icebear03.splendidenchants.enchant.EnchantLoader
import world.icebear03.splendidenchants.enchant.SplendidEnchant
import world.icebear03.splendidenchants.enchant.data.Rarity
import world.icebear03.splendidenchants.enchant.data.limitation.CheckType

fun Enchantment.splendidEt() = splendidEt(key)
fun splendidEt(identifier: String) = EnchantLoader.BY_NAME[identifier] ?: EnchantLoader.BY_ID[identifier]
fun splendidEt(key: NamespacedKey) = EnchantLoader.BY_ID[key.key]
fun splendidEts(rarity: Rarity) = EnchantLoader.BY_RARITY[rarity]?.toList() ?: listOf()

fun SplendidEnchant.book(level: Int = maxLevel) = ItemStack(Material.ENCHANTED_BOOK).modifyMeta<ItemMeta> { addEt(this@book, level) }

fun ItemStack.etsAvailable(
    checkType: CheckType = CheckType.ANVIL,
    player: Player? = null
) = EnchantLoader.BY_ID.values.filter { it.limitations.checkAvailable(checkType, this, player).first }

fun Collection<SplendidEnchant>.drawEt(): SplendidEnchant? {
    val rarity = RandomList(*associate { it.rarity to it.rarity.weight }.toList().toTypedArray()).random()
    return RandomList(*filter { rarity == it.rarity }.associateWith { it.alternativeData.weight }.toList().toTypedArray()).random()
}

fun Rarity.drawEt() = RandomList(*splendidEts(this).associateWith { it.alternativeData.weight }.toList().toTypedArray()).random()

fun SplendidEnchant.display(level: Int? = null) = (rarity.color + basicData.name + (level?.roman(maxLevel == 1, true) ?: "")).colorify()