package world.icebear03.splendidenchants.enchant.mechanism.chain

import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack
import taboolib.common.util.replaceWithOrder
import taboolib.module.kether.compileToJexl
import world.icebear03.splendidenchants.api.CooldownAPI
import world.icebear03.splendidenchants.enchant.mechanism.EventType
import world.icebear03.splendidenchants.enchant.mechanism.EventType.*
import world.icebear03.splendidenchants.enchant.mechanism.Listeners
import world.icebear03.splendidenchants.enchant.mechanism.chain.ChainType.*
import world.icebear03.splendidenchants.enchant.mechanism.entry.event.Attack
import world.icebear03.splendidenchants.enchant.mechanism.entry.event.Kill
import world.icebear03.splendidenchants.enchant.mechanism.entry.operation.Plant

data class Chain(val listeners: Listeners, val chainLine: String) {

    val belonging: Listeners = listeners
    val type = ChainType.fromString(chainLine.split("::")[0])
    val originLine = chainLine.split("::")[1]

    //注意：这里的item一定要是原物品，不能是clone的！！
    fun trigger(
        event: Event,
        eventType: EventType,
        player: Player,
        item: ItemStack,
        replacerMap: ArrayList<Pair<String, String>>
    ): Boolean {

        val line = originLine.replaceWithOrder(*replacerMap.toArray())
        val params = line.split(":")

        when (type) {
            //特殊条件：冷却，每个附魔只有一个冷却计数器
            //格式：
            //冷却::冷却时间(s):是否播报给玩家
            COOLDOWN -> {
                val cdInSec = params[0].toDouble()
                val key = belonging.belonging.name
                val info = params[1].toBoolean()

                val result = CooldownAPI.checkStamp(player, key, cdInSec, info)
                if (!result.first) {
                    if (info) {
                        player.sendMessage("冷却未结束，还有${result.second}s")
                    }
                    return false
                }
                CooldownAPI.addStamp(player, key)
            }

            CONDITION -> {
                //TODO 应当支持更多东西！
                //比如字符串相等，contains等
                return line.compileToJexl().eval() as Boolean
            }

            ASSIGNMENT -> {
                val variableName = line.split("=")[0].replace("{", "").replace("}", "")
                val expression = line.split("=")[1]
                //TODO 能不能不用try，就判断ta是不是数学表达式
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
                    KILL -> Kill.modifyEvent(event, player, params, replacerMap)
                    ATTACK -> Attack.modifyEvent(event, player, params, replacerMap)
                    RIGHT_CLICK -> TODO()
                }
            }

            OPERATION -> {
                when (params[0]) {
                    "plant" -> {
                        Plant.plant(player, params[1].toInt(), params[2])
                    }

                    else -> {}
                }
            }
        }
        return true
    }
}
