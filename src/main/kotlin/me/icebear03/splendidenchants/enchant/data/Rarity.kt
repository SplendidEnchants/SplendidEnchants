package me.icebear03.splendidenchants.enchant.data

import me.icebear03.splendidenchants.util.loadAndUpdate
import taboolib.common.platform.function.info
import taboolib.module.configuration.Configuration
import java.util.concurrent.ConcurrentHashMap

data class Rarity(
    val id: String,
    val name: String,
    val color: String,
    val weight: Double
) {
    companion object {

        val rarities = ConcurrentHashMap<String, Rarity>()

        lateinit var defaultRarity: Rarity

        fun initialize() {
            val rarityConfig = Configuration.loadAndUpdate("enchants/rarity.yml", listOf()) // TODO: 白名单列表
            rarityConfig.getKeys(false).forEach {
                rarities[it] = Rarity(
                    it,
                    rarityConfig.getString("$it.name")!!,
                    rarityConfig.getString("$it.color")!!,
                    rarityConfig.getDouble("$it.weight")
                )
            }
            info("调试信息：加载品质成功，共${rarities.size}个品质！")

            defaultRarity = rarities[""]!!
        }

        fun fromIdOrName(idOrName: String): Rarity {
            if (rarities[idOrName] != null)
                return rarities[idOrName]!!
            for (rarity in rarities.values) {
                if (rarity.name == idOrName)
                    return rarity
            }
            return defaultRarity
        }
    }
}
// TODO: attainsources -> Tradable / Discoverable