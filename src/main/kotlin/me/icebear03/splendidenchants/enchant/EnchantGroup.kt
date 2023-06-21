package me.icebear03.splendidenchants.enchant

import me.icebear03.splendidenchants.api.EnchantAPI
import me.icebear03.splendidenchants.util.loadAndUpdate
import org.bukkit.enchantments.Enchantment
import taboolib.common.platform.function.info
import taboolib.module.configuration.Configuration
import java.util.concurrent.ConcurrentHashMap

data class EnchantGroup(
    val name: String,
    val ids: List<String>
) {

    companion object {

        val groups = ConcurrentHashMap<String, EnchantGroup>()

        fun initialize() {
            val groupConfig = Configuration.loadAndUpdate("enchants/group.yml", listOf()) // TODO: 白名单列表
            groupConfig.getKeys(false).forEach {
                groups[it] = EnchantGroup(
                    it,
                    groupConfig.getStringList(it)
                )
            }
            info("调试信息：加载附魔组成功，共${groups.size}个组！")
        }

        fun isIn(enchant: Enchantment, group: String): Boolean {
            return groups[group]?.ids?.contains(EnchantAPI.getName(enchant)) == true
        }
    }
}