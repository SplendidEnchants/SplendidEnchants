package world.icebear03.splendidenchants.enchant.data

import taboolib.common.platform.function.console
import taboolib.module.configuration.Configuration
import world.icebear03.splendidenchants.Config
import world.icebear03.splendidenchants.api.internal.colorify
import world.icebear03.splendidenchants.api.internal.loadAndUpdate
import java.util.concurrent.ConcurrentHashMap

val rarities = ConcurrentHashMap<String, Rarity>()
lateinit var defaultRarity: Rarity

data class Rarity(
    val id: String,
    val name: String,
    val color: String,
    val weight: Int,
    val skull: String?
) {

    fun display(): String = "$color$name".colorify()

    companion object {
        fun load() {
            rarities.clear()

            Configuration.loadAndUpdate("enchants/rarity.yml").run {
                getKeys(false).forEach { id ->
                    rarities[id] = Rarity(
                        id,
                        getString("$id.name")!!,
                        getString("$id.color")!!,
                        getInt("$id.weight"),
                        getString("$id.skull")
                    )
                }
            }
            defaultRarity = rarities[Config.config.getString("default_rarity", "common")]!!

            console().sendMessage("    Successfully load §6${rarities.size} rarities")
        }
    }
}

fun rarity(identifier: String?): Rarity? = rarities[identifier] ?: rarities.values.find { it.name == identifier }