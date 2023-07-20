package world.icebear03.splendidenchants.util

import org.bukkit.entity.Player
import world.icebear03.splendidenchants.enchant.EnchantGroup
import world.icebear03.splendidenchants.enchant.EnchantLoader
import world.icebear03.splendidenchants.enchant.SplendidEnchant
import world.icebear03.splendidenchants.enchant.data.Rarity
import world.icebear03.splendidenchants.enchant.data.Target
import world.icebear03.splendidenchants.util.EnchantFilter.FilterType.*
import java.util.*

object EnchantFilter {

    val all = EnchantLoader.enchantByName.values
    val filterMap = mutableMapOf<UUID, MutableMap<Pair<FilterType, Any>, FilterStatement>>()

    fun test() {
        println(
            filter(
                mutableMapOf(
                    (FilterType.RARITY to Rarity.fromIdOrName("传奇")) to FilterStatement.ON
                )
            )
        )
    }


    fun filter(player: Player): List<SplendidEnchant> {
        createIfNotExists(player)
        return filter(filterMap[player.uniqueId]!!)
    }

    fun filter(filters: MutableMap<Pair<FilterType, Any>, FilterStatement>): List<SplendidEnchant> {
        return all.filter { enchant ->
            filters.filter {
                val type = it.key.first
                val value = it.key.second
                val state = it.value

                val isConsistent = when (type) {
                    RARITY -> enchant.rarity == (value as Rarity)
                    TARGET -> enchant.targets.contains(value as Target)
                    TYPE -> EnchantGroup.isIn(enchant, value.toString())
                    STRING -> enchant.basicData.name.contains(value.toString()) ||
                            enchant.displayer.generalDescription.contains(value.toString())
                }

                if (state == FilterStatement.ON)
                    isConsistent
                else
                    !isConsistent
            }
            true
        }
    }

    fun createIfNotExists(player: Player) {
        if (filterMap.containsKey(player.uniqueId))
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
        filterMap[player.uniqueId]!!.filter { it.key.second == key }.forEach {
            filterMap[player.uniqueId]!!.remove(it.key)
        }
        filterMap[player.uniqueId]!![type to key] = state
    }

    fun removeFilter(player: Player, type: FilterType) {
        createIfNotExists(player)
        filterMap[player.uniqueId]!!.filter { it.key.first == type }.forEach {
            filterMap[player.uniqueId]!!.remove(it.key)
        }
    }

    enum class FilterType(displayName: String) {
        RARITY("品质"),
        TARGET("物品类别"),
        TYPE("类型/定位"),
        STRING("名字/描述");

        val displayName: String

        init {
            this.displayName = displayName
        }
    }

    enum class FilterStatement(symbol: String) {
        ON("√"),
        OFF("×");

        val symbol: String

        init {
            this.symbol = symbol
        }
    }
}