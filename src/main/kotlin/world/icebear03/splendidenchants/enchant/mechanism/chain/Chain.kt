package world.icebear03.splendidenchants.enchant.mechanism.chain

import com.mcstarrysky.starrysky.i18n.sendLang
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.submit
import world.icebear03.splendidenchants.api.*
import world.icebear03.splendidenchants.enchant.SplendidEnchant
import world.icebear03.splendidenchants.enchant.mechanism.EventType
import world.icebear03.splendidenchants.enchant.mechanism.chain.ChainType.*
import world.icebear03.splendidenchants.enchant.mechanism.entry.internal.ObjectEntry
import world.icebear03.splendidenchants.enchant.mechanism.entry.internal.objItem
import world.icebear03.splendidenchants.enchant.mechanism.entry.internal.objString
import world.icebear03.splendidenchants.enchant.mechanism.entry.operation.Broadcast
import world.icebear03.splendidenchants.enchant.mechanism.entry.operation.Plant
import world.icebear03.splendidenchants.enchant.mechanism.entry.operation.Println

class Chain(val enchant: SplendidEnchant, line: String) {

    val type = ChainType.getType(line.split("::")[0])
    val content = line.split("::")[1]

    //注意：这里的item一定要是原物品，不能是副本
    //前两个参数在ticker trigger时为空
    @Suppress("UNCHECKED_CAST")
    fun trigger(
        event: Event?,
        eventType: EventType?,
        entity: LivingEntity,
        item: ItemStack,
        sHolders: MutableMap<String, String>,
        fHolders: MutableMap<String, Pair<ObjectEntry<*>, String>>
    ): Boolean {
        //首先全部替换
        var variabled = content.replace(sHolders).replace(fHolders.mapValues { it.value.second })

        fun getObj(nodes: List<String>, start: Pair<ObjectEntry<*>, Any?>? = null): Pair<ObjectEntry<*>, Any?> {
            var obj =
                start?.let { it.first.g(it.second, nodes[0]) } ?: if (nodes[0] == "物品") objItem.h(item)
                else fHolders[nodes[0]] ?: event?.let { eventType?.entry?.g(it, nodes[0]) } ?: objString.h(null)
            for (i in 1 until nodes.size) {
                val type = obj.first
                obj = type.g(type.d(obj.second), nodes[i])
            }
            return obj
        }

        val reg = Regex("\\{[^{}]*\\}")
        reg.findAll(variabled).forEach { result ->
            val path = result.value.replace("{" to "", "}" to "", tagged = false).split(".")
            getObj(path).second?.let {
                variabled = variabled.replace(result.value, it.toString())
            }
        }

        //生成变量
        val parts = variabled.split(":")

        val toPlayer = entity as? Player

        when (type) {
            //特殊条件：冷却，每个附魔只有一个冷却计数器
            //格式：
            //冷却::冷却时间(s):是否播报给玩家
            COOLDOWN -> {
                if (entity !is Player)
                    return true

                val cdInSec = parts[0].toDouble()
                val key = enchant.basicData.id
                val info = parts[1].toBoolean()

                val result = entity.checkCd(key, cdInSec)
                if (!result.first) {
                    if (info) entity.sendLang("messages.misc.cool_down", "second" to result.second)
                    return false
                }
                entity.addCd(key)
            }

            CONDITION -> return variabled.calcToBoolean()

            ASSIGNMENT -> {
                val tmp = enchant.variable
                if (tmp.variables[parts[0]] == SplendidEnchant.VariableType.FLEXIBLE) {
                    val pair = fHolders[parts[0]]!!
                    fHolders[parts[0]] = pair.first to parts[1]
                } else tmp.modifyVariable(item, parts[0], parts[1].calculate())
            }

            EVENT -> event?.let { eventType?.entry?.m(it, entity, parts[0], parts.subList(1)) }

            OPERATION -> when (parts[0]) {
                "plant", "播种" -> submit submit@{ Plant.plant(toPlayer ?: return@submit, parts[1].toInt(), parts[2]) }
                "println", "控制台输出" -> Println.println(entity, parts.subList(1).joinToString(""))
                "broadcast", "播报" -> Broadcast.broadcast(parts.subList(1).joinToString(""))
                else -> {}
            }

            OBJECT -> {
                fHolders[parts[0]]?.let {
                    val type = it.first
                    val obj = type.disholderize(it.second)
                    type.m(obj, parts[1], parts.subList(2))
                    val newPair = type.h(obj)
                    fHolders[parts[0]] = newPair
                } ?: run {
                    val path = parts[0].split(".")
                    val obj = getObj(path)
                    val type = obj.first
                    return type.m(type.d(obj.second), parts[1], parts.subList(2))
                }
            }

            ITEM -> objItem.modify(item, parts[0], parts.subList(1))

            TRAVERSE -> {
                val nodes = parts[0].split(".")
                val pair = getObj(nodes).second as Pair<ObjectEntry<*>, List<String>>
                val type = pair.first
                val elements = pair.second
                when (parts[1]) {
                    "存在" -> {
                        return elements.any { element ->
                            val obj = type.disholderize(element) ?: return@any false
                            var line = parts[2]
                            reg.findAll(variabled).forEach { result ->
                                val nodesInner = result.value.replace("{" to "", "}" to "", tagged = false).split(".")
                                if (nodesInner[0] == "元素") {
                                    getObj(nodesInner.subList(1), type to obj).second?.let {
                                        line = line.replace(result.value, it.toString())
                                    }
                                }
                            }
                            line.calcToBoolean()
                        }
                    }

                    "至少" -> {}
                    "修改" -> {}
                }
            }

            else -> {}
        }
        return true
    }
}
