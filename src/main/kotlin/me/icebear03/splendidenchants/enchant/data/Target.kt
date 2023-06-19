package me.icebear03.splendidenchants.enchant.data

import org.bukkit.Material
import java.util.concurrent.ConcurrentHashMap

/**
 * SplendidEnchants
 * me.icebear03.splendidenchants.enchant.data.Target
 *
 * @author HamsterYDS
 * @since 2023/6/19 9:57 PM
 */
data class Target(
    val id: String,
    val name: String,
    val capability: Int,
    val typeNames: List<String>
) {

    val types = typeNames.map { Material.valueOf(it) }

    init {
        TODO("INITIALIZE from targets.yml")
    }

    fun maxCapability(type: Material): Int {
        var ans = 99 /*TODO max in config.yml*/
        for (target in targets.values) {
            if (target.types.contains(type)) {
                ans = ans.coerceAtMost(target.capability)
            }
        }
        return ans
    }

    companion object {

        val targets = ConcurrentHashMap<String, Target>()
    }
}
