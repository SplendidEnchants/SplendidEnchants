package world.icebear03.splendidenchants.enchant.mechanism.chain

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack
import taboolib.common.util.replaceWithOrder
import taboolib.module.kether.compileToJexl
import world.icebear03.splendidenchants.enchant.mechanism.EventType
import world.icebear03.splendidenchants.enchant.mechanism.EventType.*
import world.icebear03.splendidenchants.enchant.mechanism.Listeners
import world.icebear03.splendidenchants.enchant.mechanism.chain.ChainType.*
import world.icebear03.splendidenchants.enchant.mechanism.entry.event.Attack
import world.icebear03.splendidenchants.enchant.mechanism.entry.event.Kill

data class Chain(val listeners: Listeners, val chainLine: String) {

    val belonging: Listeners = listeners
    val type = ChainType.fromString(chainLine.split("::")[0])
    val line = chainLine.split("::")[1]

    //注意：这里的item一定要是原物品，不能是clone的！！
    fun trigger(
        event: Event,
        eventType: EventType,
        player: Player,
        item: ItemStack,
        replacerMap: ArrayList<Pair<String, String>>
    ): Boolean {
        when (type) {
            CONDITION -> {
                //TODO 应当支持更多东西！
                //比如字符串相等，contains等
                val newLine = line.replaceWithOrder(*replacerMap.toArray())
                println(newLine)
                return newLine.compileToJexl().eval() as Boolean
            }

            ASSIGNMENT -> {
                val variableName = line.split("=")[0].replace("{", "").replace("}", "")
                val expression = line.split("=")[1].replaceWithOrder(*replacerMap.toArray())
                val variableValue =
                    try {
                        expression.compileToJexl().eval().toString()
                    } catch (e: Exception) {
                        expression
                    }
                belonging.belonging.variable.modifyVariable(item, variableName, variableValue)
            }

            EVENT -> {
                when (eventType) {
                    KILL -> Kill.modifyEvent(event, player, line, replacerMap)
                    ATTACK -> Attack.modifyEvent(event, player, line, replacerMap)
                    RIGHT_CLICK -> TODO()
                }
            }

            OPERATION -> {

            }
        }
        return true
    }
}
