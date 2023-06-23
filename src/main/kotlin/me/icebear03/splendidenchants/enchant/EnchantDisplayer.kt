package me.icebear03.splendidenchants.enchant

import me.icebear03.splendidenchants.Config

object EnchantDisplayer {
    var defaultPrevious: String
    var defaultSubsequent: String

    var sortByLevel: Boolean
    var sortByRarity: Boolean
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
        sortByRarity = config.getBoolean("sort.rarity.enable", true)
        rarityOrder = config.getStringList("sort.rarity.order")
        combine = config.getBoolean("combine.enable", false)
        minimal = config.getInt("combine.min", 8)
        amount = config.getInt("combine.amount", 2)
        layout = config.getStringList("combine.layout")
    }

    //TODO 妈的，下面是巨量工程
}