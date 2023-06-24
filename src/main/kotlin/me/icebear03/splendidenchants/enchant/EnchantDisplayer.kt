package me.icebear03.splendidenchants.enchant

import me.icebear03.splendidenchants.Config
import me.icebear03.splendidenchants.enchant.data.Rarity

object EnchantDisplayer {

    var defaultPrevious: String
    var defaultSubsequent: String

    var sortByLevel: Boolean
    var rarityOrder: List<String>

    var combine: Boolean
    var minimal: Int
    var amount: Int
    var layout: List<String>

    init {
        val config = Config.config.getConfigurationSection("display")!!
        defaultPrevious = config.getString("format.default_previous", "{color}{name} {roman_level}")!!
        defaultSubsequent = config.getString("format.default_subsequent", "\n§8| §7{description}")!!
        sortByLevel = config.getBoolean("sort.level", true)
        rarityOrder = config.getStringList("sort.rarity.order")
        Rarity.rarities.keys.forEach {
            if (!rarityOrder.contains(it))
                rarityOrder += it
        }

        combine = config.getBoolean("combine.enable", false)
        minimal = config.getInt("combine.min", 8)
        amount = config.getInt("combine.amount", 2)
        layout = config.getStringList("combine.layout")
    }

    //TODO 妈的，下面是巨量工程

    fun sortEnchants(enchants: Map<SplendidEnchant, Int>): LinkedHashMap<SplendidEnchant, Int> {
        return LinkedHashMap(enchants.toSortedMap(Comparator.comparing {
            return@comparing rarityOrder.indexOf(it.rarity.id) * 100000 +
                    (if (sortByLevel) enchants[it]!! else 0)
        }))
    }

    //TODO 生成lore模块，此处包含combine

    //TODO 修改物品模块，注意PDC

    //TODO 回退物品模块，注意PDC，防止NBT堆叠造成卡顿
    //TODO 应注意创造模式的转换
}