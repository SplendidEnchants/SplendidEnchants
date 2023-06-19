package me.icebear03.splendidenchants.enchant.data

import org.bukkit.Material

class Target private constructor(
    private val id: String, private val name: String, //该种物品最大的附魔容纳量
    private val capability: Int, types: List<String>
) {
    private val types: MutableList<Material> = ArrayList()

    init {
        for (string in types) {
            this.types.add(Material.valueOf(string))
        }
        targets[id] = this
    }

    fun name(): String {
        return name
    }

    fun isIn(type: Material): Boolean {
        return types.contains(type)
    }

    companion object {
        var targets = HashMap<String, Target>()
        fun maxCapability(type: Material): Int {
            var ans = 99 /*TODO max in config.yml*/
            for (target in targets.values) {
                if (target.types.contains(type)) {
                    ans = Math.min(ans, target.capability)
                }
            }
            return ans
        }
    }
}
