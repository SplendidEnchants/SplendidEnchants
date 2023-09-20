package world.icebear03.splendidenchants.player

import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import taboolib.common5.clong
import world.icebear03.splendidenchants.api.get
import world.icebear03.splendidenchants.api.set
import world.icebear03.splendidenchants.api.splendidEt
import world.icebear03.splendidenchants.enchant.EnchantFilter
import world.icebear03.splendidenchants.player.internal.MenuMode
import java.util.*

data class PlayerData(private val serializedData: String?) {
    var menuMode: MenuMode = MenuMode.NORMAL
    var favorites: MutableList<String> = mutableListOf()
    var filters: Map<EnchantFilter.FilterType, MutableMap<String, EnchantFilter.FilterStatement>> =
        EnchantFilter.FilterType.entries.associateWith { mutableMapOf() }
    var cooldown: MutableMap<String, Long> = mutableMapOf()

    init {
        serializedData?.let {
            serializedData.split("||")
                .map { pair -> pair.split("==")[0] to pair.split("==")[1] }
                .forEach { (key, value) ->
                    when (key) {
                        "menu_mode" -> menuMode = MenuMode.valueOf(value)
                        "favorites" -> value.split(";").mapNotNull { id -> splendidEt(id) }
                        "filters" -> {
                            var tot = 0
                            value.split("$").forEach { content ->
                                filters[EnchantFilter.filterTypes[tot++]]!!.putAll(content.split(";")
                                    .filter { filter -> filter.isNotBlank() }
                                    .associate { filter ->
                                        filter.split("=")[0] to
                                                EnchantFilter.FilterStatement.valueOf(filter.split("=")[1])
                                    })
                            }
                        }

                        "cooldown" -> {
                            cooldown.putAll(value
                                .split(";")
                                .filter { pair -> pair.isNotBlank() }
                                .associate { pair -> pair.split("=")[0] to pair.split("=")[1].clong })
                        }

                        else -> {}
                    }
                }
        }
    }

    fun serialize() = "menu_mode==$menuMode||" +
            "favorites==${favorites.joinToString(";")}||" +
            "filters==${
                EnchantFilter.filterTypes.map {
                    filters[it]!!.map { (value, state) -> "$value=$state" }.joinToString(";")
                }.joinToString("$")
            }||" +
            "cooldown==${cooldown.map { (id, stamp) -> "$id=$stamp" }.joinToString { ";" }}"
}

val data = mutableMapOf<UUID, PlayerData>()

fun Player.loadSEData() {
    data[uniqueId] = PlayerData(get("splendidenchants_data", PersistentDataType.STRING))
}

fun Player.saveSEData() {
    data[uniqueId]?.let {
        set("splendidenchants_data", PersistentDataType.STRING, it.serialize())
        println(it.serialize())
    }
}

var Player.menuMode
    get() = data[uniqueId]!!.menuMode
    set(value) {
        data[uniqueId]!!.menuMode = value
    }
val Player.favorites get() = data[uniqueId]!!.favorites
val Player.filters get() = data[uniqueId]!!.filters
val Player.cooldown get() = data[uniqueId]!!.cooldown