package me.icebear03.splendidenchants.enchant.data

import me.icebear03.splendidenchants.Config
import org.bukkit.Material
import org.bukkit.inventory.EquipmentSlot
import taboolib.common.platform.function.info
import taboolib.module.configuration.Configuration
import java.util.concurrent.ConcurrentHashMap

data class Target(
    val id: String,
    val name: String,
    val capability: Int,
    val activeSlots: Set<EquipmentSlot>,
    val typeNames: List<String>
) {

    val types = typeNames.map { Material.valueOf(it) }

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

        fun initialize() {
            val targetConfig: Configuration = Config.updateAndGetResource("enchants/target.yml")
            for (id in targetConfig.getKeys(false)) run {
                val activeSlots = HashSet<EquipmentSlot>()
                for (string in targetConfig.getStringList("$id.active_slots")) {
                    activeSlots.add(EquipmentSlot.valueOf(string))
                }
                val target = Target(
                    id,
                    targetConfig.getString("$id.name")!!,
                    targetConfig.getInt("$id.max")!!,
                    activeSlots,
                    targetConfig.getStringList("$id.types")
                )
                targets.put(id, target)
            }
            info("调试信息：加载附魔对象成功，共${Rarity.rarities.size}种对象！")
        }
    }
}
