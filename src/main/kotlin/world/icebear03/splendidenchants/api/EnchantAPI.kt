package world.icebear03.splendidenchants.api

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import taboolib.common5.RandomList
import taboolib.platform.util.modifyMeta
import world.icebear03.splendidenchants.enchant.EnchantLoader
import world.icebear03.splendidenchants.enchant.SplendidEnchant
import world.icebear03.splendidenchants.enchant.data.Rarity
import world.icebear03.splendidenchants.enchant.data.limitation.CheckType

fun Enchantment.splendidEt() = splendidEt(key)!!
fun splendidEt(identifier: String) = EnchantLoader.BY_NAME[identifier] ?: EnchantLoader.BY_ID[identifier]
fun splendidEt(key: NamespacedKey) = EnchantLoader.BY_ID[key.key]
fun splendidEts(rarity: Rarity) = EnchantLoader.BY_RARITY[rarity]?.toList() ?: listOf()

fun SplendidEnchant.book(level: Int = maxLevel) = ItemStack(Material.ENCHANTED_BOOK).modifyMeta<ItemMeta> { addEt(this@book, level) }

fun etsAvailable(
    checkType: CheckType = CheckType.ANVIL,
    item: ItemStack,
    player: Player? = null
) = EnchantLoader.BY_ID.values.filter { it.limitations.checkAvailable(checkType, item, player).first }

fun Collection<SplendidEnchant>.drawEt() = RandomList(*associate { it.rarity to it.rarity.weight }.toList().toTypedArray()).random()?.drawEt()
fun Rarity.drawEt() = RandomList(*splendidEts(this).associateWith { it.alternativeData.weight }.toList().toTypedArray()).random()