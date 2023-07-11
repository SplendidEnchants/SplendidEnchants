package world.icebear03.splendidenchants.enchant.mechanism.chain

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack
import taboolib.common.util.replaceWithOrder
import taboolib.module.kether.compileToJexl
import world.icebear03.splendidenchants.enchant.mechanism.Listeners
import world.icebear03.splendidenchants.enchant.mechanism.chain.ChainType.*

data class Chain(val listeners: Listeners, val chainLine: String) {

    val belonging: Listeners = listeners
    val type = ChainType.fromString(chainLine.split("::")[0])
    val line = chainLine.split("::")[1]

    //注意：这里的item一定要是原物品，不能是clone的！！
    fun trigger(event: Event, player: Player, item: ItemStack, replacerMap: Array<Pair<String, String>>): Boolean {
        when (type) {
            CONDITION -> {
                //TODO 应当支持更多东西！
                //比如字符串相等，contains等
                val newLine = line.replaceWithOrder(*replacerMap)
                return newLine.compileToJexl().eval() as Boolean
            }

            ASSIGNMENT -> {
                val variableName = line.split("=")[0].replace("{", "").replace("}", "")
                val variableValue = line.split("=")[1].replaceWithOrder(*replacerMap).compileToJexl().eval() as String
                belonging.belonging.variable.modifyVariable(item, variableName, variableValue)
            }

            EVENT -> {

            }

            OPERATION -> TODO()
        }
        return true
    }
}
