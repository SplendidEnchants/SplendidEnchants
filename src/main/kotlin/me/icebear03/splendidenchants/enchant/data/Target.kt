package me.icebear03.splendidenchants.enchant.data

import org.bukkit.Material
import org.bukkit.inventory.EquipmentSlot
import java.util.concurrent.ConcurrentHashMap

data class Target(
    val id: String,
    val name: String,
    val capability: Int,
    val slot: EquipmentSlot,
    val typeNames: List<String>
) {

    val types = typeNames.map { Material.valueOf(it) }

    init {
        TODO("INITIALIZE from target.yml")
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

    val targets = ConcurrentHashMap<String, Target>()
}
