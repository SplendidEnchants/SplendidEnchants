package world.icebear03.splendidenchants.enchant.data.limitation

import org.bukkit.Material
import org.bukkit.inventory.EquipmentSlot
import taboolib.common.platform.function.info
import taboolib.module.configuration.Configuration
import world.icebear03.splendidenchants.util.loadAndUpdate
import java.util.concurrent.ConcurrentHashMap

data class Target(
    val id: String,
    val name: String,
    val capability: Int,
    val activeSlots: Set<EquipmentSlot>,
    val typeNames: List<String>
) {

    val types = typeNames.map { Material.valueOf(it) }

    companion object {

        val targets = ConcurrentHashMap<String, Target>()

        fun initialize() {
            val targetConfig = Configuration.loadAndUpdate("enchants/target.yml")
            targetConfig.getKeys(false).forEach {
                targets[it] = Target(
                    it,
                    targetConfig.getString("$it.name")!!,
                    targetConfig.getInt("$it.max"),
                    targetConfig.getStringList("$it.active_slots").map { s -> EquipmentSlot.valueOf(s) }.toSet(),
                    targetConfig.getStringList("$it.types")
                )
            }
            targets["unknown"] = Target("unknown", "未定义", 16, hashSetOf(), arrayListOf())
            info("调试信息：加载附魔对象成功，共${targets.size}种对象！")
        }

        fun fromIdOrName(idOrName: String): Target {
            return targets[idOrName] ?: targets.values.firstOrNull { it.name == idOrName } ?: targets["unknown"]!!
        }

        fun maxCapability(type: Material): Int {
            var ans = 32
            for (target in targets.values) {
                if (target.types.contains(type)) {
                    ans = ans.coerceAtMost(target.capability)
                }
            }
            return ans
        }

        fun isIn(it: Target, type: Material): Boolean {
            return it.types.contains(type)
        }
    }
}
