package world.icebear03.splendidenchants.enchant

import org.bukkit.enchantments.Enchantment
import taboolib.common.platform.function.info
import taboolib.module.configuration.Configuration
import world.icebear03.splendidenchants.api.EnchantAPI
import world.icebear03.splendidenchants.enchant.data.Rarity
import world.icebear03.splendidenchants.util.loadAndUpdate
import java.util.concurrent.ConcurrentHashMap

data class EnchantGroup(
    val name: String,
    val enchantNames: List<String>,
    val maxCoexist: Int
) {

    val enchants = mutableListOf<SplendidEnchant>()

    init {
        enchantNames.forEach {
            val enchant = EnchantAPI.getSplendidEnchant(it)
            if (enchant != null)
                enchants += enchant
        }
    }

    companion object {

        val groups = ConcurrentHashMap<String, EnchantGroup>()

        fun initialize() {
            val groupConfig = Configuration.loadAndUpdate("enchants/group.yml")
            groupConfig.getKeys(false).forEach {
                val enchants = mutableListOf<String>()
                if (groupConfig.contains("$it.enchants")) {
                    enchants += groupConfig.getStringList("$it.enchants").toMutableList()
                }

                if (groupConfig.contains("$it.rarities")) {
                    groupConfig.getStringList("$it.rarities").forEach {
                        val rarity = Rarity.fromIdOrName(it)
                        EnchantAPI.getSplendidEnchants(rarity).forEach { enchant ->
                            enchants += enchant.basicData.name
                        }
                    }
                }

                groups[it] = EnchantGroup(
                    it,
                    enchants,
                    groupConfig.getInt("$it.max_coexist", 1)
                )
            }
            info("调试信息：加载附魔组成功，共${groups.size}个组！")
        }

        fun isIn(enchant: Enchantment, group: String): Boolean {
            return if (groups[group] == null) false
            else groups[group]!!.enchantNames.contains(EnchantAPI.getName(enchant))
                    || groups[group]!!.enchantNames.contains(EnchantAPI.getId(enchant))
        }

        fun maxCoexist(group: String): Int {
            return if (groups[group] == null) 1
            else groups[group]!!.maxCoexist
        }

        fun getSplendidEnchants(group: String): List<SplendidEnchant> {
            if (!groups.containsKey(group))
                return listOf()
            return groups[group]!!.enchants
        }
    }
}