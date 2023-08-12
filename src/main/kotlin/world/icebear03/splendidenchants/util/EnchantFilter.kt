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

    val all = EnchantLoader.BY_NAME.values
    val filterMap = mutableMapOf<UUID, MutableMap<FilterType, MutableList<Pair<Any, FilterStatement>>>>()

    fun filter(player: Player): List<SplendidEnchant> {
        createIfNotExists(player)
        return filter(filterMap[player.uniqueId]!!)
    }

    fun filter(filters: MutableMap<FilterType, MutableList<Pair<Any, FilterStatement>>>): List<SplendidEnchant> {
        return all.filter { enchant ->
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
                            TARGET -> {
                                val target = pair.first as Target
                                if (enchant.targets.contains(pair.first as Target)) true
                                else {
                                    var flag = true
                                    enchant.targets.forEach {
                                        if (!target.types.containsAll(it.types))
                                            flag = false
                                    }
                                    flag
                                }
                            }

                            GROUP -> EnchantGroup.isIn(enchant, pair.first as EnchantGroup)
                            STRING -> {
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
                        }
                    }
                }

                if (!onExists)
                    onFlag = true

                if (offFlag)
                    flag = false
                if (!onFlag)
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

                GROUP -> {
                    val group = it.first as EnchantGroup
                    group.name
                }

                STRING -> {
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

    fun clearFilter(player: Player, type: FilterType) {
        createIfNotExists(player)
        filterMap[player.uniqueId]!![type] = mutableListOf()
    }

    fun clearAll() {
        filterMap.clear()
    }

    fun getStatement(player: Player, type: FilterType, value: Any): FilterStatement? {
        createIfNotExists(player)
        val pairs = filterMap[player.uniqueId]!![type] ?: return null
        pairs.forEach {
            if (it.first == value)
                return it.second
        }
        return null
    }

    fun addFilter(player: Player, type: FilterType, value: String, state: FilterStatement) {
        addFilter(
            player, type, when (type) {
                RARITY -> Rarity.fromIdOrName(value)
                TARGET -> Target.fromIdOrName(value)
                GROUP -> EnchantGroup.fromName(value)
                STRING -> value
            }, state
        )
    }

    fun addFilter(player: Player, type: FilterType, value: Any, state: FilterStatement) {
        createIfNotExists(player)
        if (!filterMap[player.uniqueId]!!.containsKey(type))
            filterMap[player.uniqueId]!![type] = mutableListOf()
        filterMap[player.uniqueId]!![type]!! += value to state
    }

    fun clearFilter(player: Player, type: FilterType, value: Any) {
        createIfNotExists(player)
        val pairs = filterMap[player.uniqueId]!![type] ?: return
        pairs.toList().forEach {
            if (it.first == value)
                pairs.remove(it)
        }
    }

    enum class FilterType(val displayName: String) {
        RARITY("品质"),
        TARGET("物品类别"),
        GROUP("类型/定位"),
        STRING("名字/描述");
    }

    enum class FilterStatement(val symbol: String) {
        ON("§a✓"),
        OFF("§c✗");
    }
}