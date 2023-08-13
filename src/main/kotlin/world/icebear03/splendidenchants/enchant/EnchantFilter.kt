package world.icebear03.splendidenchants.enchant

import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import taboolib.common.platform.event.SubscribeEvent
import world.icebear03.splendidenchants.enchant.EnchantFilter.FilterStatement.OFF
import world.icebear03.splendidenchants.enchant.EnchantFilter.FilterStatement.ON
import world.icebear03.splendidenchants.enchant.EnchantFilter.FilterType.*
import world.icebear03.splendidenchants.enchant.data.*
import world.icebear03.splendidenchants.enchant.data.Target
import java.util.*

object EnchantFilter {

    val filters = mutableMapOf<UUID, Map<FilterType, LinkedHashMap<Any, FilterStatement>>>()

    fun filter(player: Player): List<SplendidEnchant> {
        return filter(filters[player.uniqueId]!!)
    }

    fun filter(filters: Map<FilterType, LinkedHashMap<Any, FilterStatement>>): List<SplendidEnchant> {
        return EnchantLoader.BY_ID.values.filter result@{ enchant ->
            filters.forEach { (type, rules) ->
                var onFlag = false
                var offFlag = false
                val onExists = rules.any { it.value == ON }

                rules.forEach { (value, state) ->
                    if (when (type) {
                            RARITY -> value as Rarity == enchant.rarity
                            TARGET -> enchant.targets.contains(value as Target)
                            GROUP -> enchant.isIn(value as Group)
                            STRING -> {
                                enchant.basicData.name.contains(value.toString()) ||
                                        enchant.basicData.id.contains(value.toString()) ||
                                        enchant.displayer.generalDescription.contains(value.toString())
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

    fun generateLore(type: FilterType, player: Player): List<String> = generateLore(type, filters[player.uniqueId]!![type]!!)

    fun generateLore(type: FilterType, rules: LinkedHashMap<Any, FilterStatement>): List<String> {
        return rules.map { (value, state) ->
            state.symbol + " " + when (type) {
                RARITY -> (value as Rarity).display()
                TARGET -> (value as Target).name
                GROUP -> (value as Group).name
                STRING -> value
            }
        }
    }

    fun clearFilters(player: Player) {
        filters[player.uniqueId] = FilterType.entries.associateWith { linkedMapOf() }
    }

    fun clearFilter(player: Player, type: FilterType) = filters[player.uniqueId]!![type]!!.clear()

    fun getStatement(player: Player, type: FilterType, value: Any): FilterStatement? = filters[player.uniqueId]!![type]!![value]


    fun addFilter(player: Player, type: FilterType, value: String, state: FilterStatement) {
        val key: Any? = when (type) {
            RARITY -> rarity(value)
            TARGET -> target(value)
            GROUP -> group(value)
            STRING -> value
        }
        key?.let { addFilter(player, type, it, state) }
    }

    fun addFilter(player: Player, type: FilterType, value: Any, state: FilterStatement) {
        filters[player.uniqueId]!![type]!![value] = state
    }

    fun clearFilter(player: Player, type: FilterType, value: Any) = filters[player.uniqueId]!![type]!!.remove(value)

    @SubscribeEvent
    fun join(event: PlayerJoinEvent) {
        filters[event.player.uniqueId] = FilterType.entries.associateWith { linkedMapOf() }
    }

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