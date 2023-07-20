package world.icebear03.splendidenchants.util

import org.bukkit.entity.Player
import world.icebear03.splendidenchants.enchant.EnchantGroup
import world.icebear03.splendidenchants.enchant.EnchantLoader
import world.icebear03.splendidenchants.enchant.SplendidEnchant
import world.icebear03.splendidenchants.enchant.data.Rarity
import world.icebear03.splendidenchants.enchant.data.Target
import world.icebear03.splendidenchants.util.EnchantFilter.FilterStatement.OFF
import world.icebear03.splendidenchants.util.EnchantFilter.FilterStatement.ON
import world.icebear03.splendidenchants.util.EnchantFilter.FilterType.*
import java.util.*

object EnchantFilter {

    val all = EnchantLoader.enchantByName.values
    val filterMap = mutableMapOf<UUID, MutableMap<FilterType, MutableList<Pair<Any, FilterStatement>>>>()

    fun filter(player: Player): List<SplendidEnchant> {
        createIfNotExists(player)
        return filter(filterMap[player.uniqueId]!!)
    }

    fun filter(filters: MutableMap<FilterType, MutableList<Pair<Any, FilterStatement>>>): List<SplendidEnchant> {
        return all.filter { enchant ->
            println("--------------------------")
            var flag = true

            filters.forEach {
                var onFlag = false
                var offFlag = false

                var onExists = false
                it.value.forEach { pair ->
                    if (pair.second == ON)
                        onExists = true
                }

                it.value.forEach { pair ->
                    run {
                        val result = when (it.key) {
                            RARITY -> pair.first as Rarity == enchant.rarity
                            TARGET -> enchant.targets.contains(pair.first as Target)
                            TYPE -> EnchantGroup.isIn(enchant, pair.first.toString())
                            STRING -> {
                                println(enchant.basicData.name + " " + pair.first.toString())
                                enchant.basicData.name.contains(pair.first.toString()) ||
                                        enchant.basicData.id.contains(pair.first.toString()) ||
                                        enchant.displayer.generalDescription.contains(pair.first.toString())
                            }
                        }
                        if (result) {
                            when (pair.second) {
                                ON -> onFlag = true
                                OFF -> offFlag = true
                            }
                            println(enchant.basicData.name + "满足：" + it.key + " 这是" + pair.second)
                        }
                    }
                }

                if (!onExists)
                    onFlag = true

                println("       $onFlag $offFlag")
                if (!onFlag || offFlag)
                    flag = false
            }
            flag
        }
    }


    fun generateLore(type: FilterType, player: Player): List<String> {
        if (!filterMap.containsKey(player.uniqueId))
            return listOf()
        val pairs = filterMap[player.uniqueId]!![type] ?: return listOf()

        return generateLore(type, pairs)
    }

    fun generateLore(type: FilterType, pairs: MutableList<Pair<Any, FilterStatement>>): List<String> {
        val lore = mutableListOf<String>()

        pairs.forEach {
            lore += it.second.symbol + " " + when (type) {
                RARITY -> {
                    val rarity = it.first as Rarity
                    rarity.color + rarity.name
                }

                TARGET -> {
                    val target = it.first as Target
                    target.name
                }

                TYPE, STRING -> {
                    it.first
                }
            }
        }
        return lore
    }

    fun createIfNotExists(player: Player) {
        if (!filterMap.containsKey(player.uniqueId))
            filterMap[player.uniqueId] = mutableMapOf()
    }

    fun clearFilter(player: Player) {
        filterMap[player.uniqueId] = mutableMapOf()
    }

    fun addFilter(player: Player, type: FilterType, value: String, state: FilterStatement) {
        createIfNotExists(player)
        val key: Any = when (type) {
            RARITY -> Rarity.fromIdOrName(value)
            TARGET -> Target.fromIdOrName(value)
            TYPE -> value
            STRING -> value
        }
        if (!filterMap[player.uniqueId]!!.containsKey(type))
            filterMap[player.uniqueId]!![type] = mutableListOf()
        filterMap[player.uniqueId]!![type]!! += key to state
    }

    fun removeFilter(player: Player, type: FilterType) {
        createIfNotExists(player)
        filterMap[player.uniqueId]!![type] = mutableListOf()
    }

    enum class FilterType(val displayName: String) {
        RARITY("品质"),
        TARGET("物品类别"),
        TYPE("类型/定位"),
        STRING("名字/描述");
    }

    enum class FilterStatement(val symbol: String) {
        ON("§7√"),
        OFF("§c×");
    }
}