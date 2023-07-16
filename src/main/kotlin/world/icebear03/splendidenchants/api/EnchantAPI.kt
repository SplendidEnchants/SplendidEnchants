package world.icebear03.splendidenchants.api

import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.util.random
import taboolib.common5.RandomList
import world.icebear03.splendidenchants.enchant.EnchantLoader
import world.icebear03.splendidenchants.enchant.SplendidEnchant
import world.icebear03.splendidenchants.enchant.data.Rarity
import world.icebear03.splendidenchants.enchant.data.limitation.CheckType

object EnchantAPI {

    fun getSplendidEnchants(rarity: Rarity): List<SplendidEnchant> {
        return EnchantLoader.enchantsByRarity[rarity]!!.toList()
    }

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

    fun getAvailableEnchants(
        player: Player,
        item: ItemStack,
        checkType: CheckType = CheckType.ANVIL
    ): List<SplendidEnchant> {
        return EnchantLoader.enchantById.values.filter {
            it.limitations.checkAvailable(
                checkType,
                player,
                item
            ).first
        }.toList()
    }

    fun drawInRandom(enchants: List<SplendidEnchant>): SplendidEnchant? {
        //首先先统计品质种类
        //抽出一个品质
        val randomRarityList = RandomList<Rarity>()
        val tmp = mutableSetOf<Rarity>()
        enchants.forEach {
            if (!tmp.contains(it.rarity)) {
                randomRarityList.add(it.rarity, it.rarity.weight)
                tmp += it.rarity
            }
        }

        val rarity = randomRarityList.random() ?: return null

        //再抽取附魔
        val randomEnchantList = RandomList<SplendidEnchant>()
        enchants.filter { it.rarity == rarity }.forEach {
            randomEnchantList.add(it, it.alternativeData.weight)
        }

        return randomEnchantList.random()
    }

    fun randomEnchant(rarity: Rarity?, lv: Int?): Pair<SplendidEnchant, Int> {
        val enchant = if (rarity == null) {
            EnchantLoader.enchantById.values.random()
        } else {
            EnchantLoader.enchantsByRarity[rarity]!!.random()
        }
        val level = lv ?: random(enchant.startLevel, enchant.maxLevel)
        return enchant to level

    }
}