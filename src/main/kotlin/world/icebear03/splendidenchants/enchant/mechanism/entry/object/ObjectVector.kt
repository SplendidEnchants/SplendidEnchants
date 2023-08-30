package world.icebear03.splendidenchants.enchant.mechanism.entry.`object`

import org.bukkit.util.Vector
import world.icebear03.splendidenchants.api.add
import world.icebear03.splendidenchants.api.calcToDouble
import world.icebear03.splendidenchants.api.numbers
import world.icebear03.splendidenchants.enchant.mechanism.entry.internal.ObjectEntry
import world.icebear03.splendidenchants.enchant.mechanism.entry.internal.objString
import world.icebear03.splendidenchants.enchant.mechanism.entry.internal.objVector

object ObjectVector : ObjectEntry<Vector>() {

    override fun modify(obj: Vector, cmd: String, params: List<String>): Boolean {
        when (cmd) {
            "单位向量" -> obj.normalize()
            "数乘" -> obj.multiply(params[0].calcToDouble())
            "数除" -> obj.multiply(params[0].calcToDouble())
            "数加" -> obj.add(params[0].calcToDouble(), params[1].calcToDouble(), params[2].calcToDouble())
            "数减" -> obj.add(-params[0].calcToDouble(), -params[1].calcToDouble(), -params[2].calcToDouble())
            "加" -> obj.add(objVector.disholderize(params[0]))
            "减" -> obj.subtract(objVector.disholderize(params[0]))
            "x" -> obj.x = params[0].calcToDouble()
            "y" -> obj.y = params[0].calcToDouble()
            "z" -> obj.z = params[0].calcToDouble()
        }
        return true
    }

    override fun get(from: Vector, objName: String): Pair<ObjectEntry<*>, Any?> {
        return when (objName) {
            "复制向量" -> objVector.holderize(from.clone())
            else -> objString.h(null)
        }
    }

    override fun holderize(obj: Vector): Pair<ObjectEntry<Vector>, String> {
        return this to "向量=${obj.x},${obj.y},${obj.z}"
    }

    override fun disholderize(holder: String): Vector {
        val numbers = holder.numbers
        return Vector(numbers[0], numbers[1], numbers[2])
    }
}