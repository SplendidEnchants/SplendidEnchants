package me.icebear03.splendidenchants.enchant.data

import me.icebear03.splendidenchants.Config
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

        fun initialize() {
            val rarityConfig: Configuration = Config.updateAndGetResource("enchants/rarity.yml")
            for (id in rarityConfig.getKeys(false)) run {
                var rarity = Rarity(
                    id,
                    rarityConfig.getString("$id.name")!!,
                    rarityConfig.getString("$id.color")!!,
                    rarityConfig.getDouble("$id.weight")
                )
                rarities.put(id, rarity)
            }
            info("调试信息：加载品质成功，共${rarities.size}个品质！")
        }
    }
}
// TODO: attainsources -> Tradable / Discoverable