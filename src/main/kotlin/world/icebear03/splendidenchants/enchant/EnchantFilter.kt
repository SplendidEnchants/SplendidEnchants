package world.icebear03.splendidenchants.enchant

import org.bukkit.entity.Player
import world.icebear03.splendidenchants.enchant.EnchantFilter.FilterStatement.OFF
import world.icebear03.splendidenchants.enchant.EnchantFilter.FilterStatement.ON
import world.icebear03.splendidenchants.enchant.EnchantFilter.FilterType.*
import world.icebear03.splendidenchants.enchant.data.group
import world.icebear03.splendidenchants.enchant.data.isIn
import world.icebear03.splendidenchants.enchant.data.rarity
import world.icebear03.splendidenchants.enchant.data.target
import world.icebear03.splendidenchants.player.filters

object EnchantFilter {

    fun filter(filters: Map<FilterType, Map<String, FilterStatement>>): List<SplendidEnchant> {
        return EnchantLoader.BY_ID.values.filter result@{ enchant ->
            filters.forEach { (type, rules) ->
                var onFlag = false
                var offFlag = false
                val onExists = rules.any { it.value == ON }

                rules.forEach { (value, state) ->
                    if (when (type) {
                            RARITY -> rarity(value) == enchant.rarity
                            TARGET -> (enchant.targets.contains(target(value)) || enchant.targets.any { target(value)?.types?.containsAll(it.types) == true })
                            GROUP -> enchant.isIn(group(value))
                            STRING -> {
                                enchant.basicData.name.contains(value) ||
                                        enchant.basicData.id.contains(value) ||
                                        enchant.displayer.generalDescription.contains(value)
                            }
                        }
                    ) {
                        when (state) {
                            ON -> onFlag = true
                            OFF -> offFlag = true
                        }
                    }
                }
                if (offFlag) return@result false
                if (!onFlag && onExists) return@result false
            }
            true
        }
    }

    fun generateLore(type: FilterType, player: Player): List<String> = generateLore(type, player.filters[type]!!)

    fun generateLore(type: FilterType, rules: Map<String, FilterStatement>): List<String> {
        return rules.map { (value, state) ->
            state.symbol + " " + when (type) {
                RARITY -> rarity(value)?.display() ?: value
                TARGET -> target(value)?.name ?: value
                GROUP -> group(value)?.name ?: value
                STRING -> value
            }
        }
    }

    fun clearFilters(player: Player) {
        filterTypes.forEach {
            player.filters[it]!!.clear()
        }
    }

    fun clearFilter(player: Player, type: FilterType) = player.filters[type]!!.clear()

    fun getStatement(player: Player, type: FilterType, value: String): FilterStatement? = player.filters[type]!![value]


    fun addFilter(player: Player, type: FilterType, value: String, state: FilterStatement) {
        player.filters[type]!![value] = state
    }

    fun clearFilter(player: Player, type: FilterType, value: Any) = player.filters[type]!!.remove(value)


    val filterTypes = FilterType.entries.toList()

    enum class FilterType(val display: String) {
        RARITY("品质"),
        TARGET("物品类别"),
        GROUP("类型/定位"),
        STRING("名字/描述");
    }

    enum class FilterStatement(val symbol: String) {
        ON("§a✔"),
        OFF("§c✘");
    }
}