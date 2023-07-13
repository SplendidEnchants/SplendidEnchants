package world.icebear03.splendidenchants.enchant.data

import taboolib.common.platform.function.info
import taboolib.module.configuration.Configuration
import world.icebear03.splendidenchants.Config
import world.icebear03.splendidenchants.util.loadAndUpdate
import java.util.concurrent.ConcurrentHashMap

data class Rarity(
    val id: String,
    val name: String,
    val color: String,
    val weight: Int
) {
    companion object {

        val rarities = ConcurrentHashMap<String, Rarity>()

        lateinit var defaultRarity: Rarity

        fun initialize() {
            val rarityConfig = Configuration.loadAndUpdate("enchants/rarity.yml")
            rarityConfig.getKeys(false).forEach {
                rarities[it] = Rarity(
                    it,
                    rarityConfig.getString("$it.name")!!,
                    rarityConfig.getString("$it.color")!!,
                    rarityConfig.getInt("$it.weight")
                )
            }
            info("调试信息：加载品质成功，共${rarities.size}个品质！")

            defaultRarity = rarities[Config.config.getString("default_rarity", "common")]!!
        }

        fun fromIdOrName(idOrName: String): Rarity {
            return rarities[idOrName] ?: rarities.values.firstOrNull { it.name == idOrName } ?: defaultRarity
        }
    }
}