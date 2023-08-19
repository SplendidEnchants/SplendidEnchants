package world.icebear03.splendidenchants.enchant.mechanism.chain

import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.function.submit
import world.icebear03.splendidenchants.api.*
import world.icebear03.splendidenchants.enchant.mechanism.EventType
import world.icebear03.splendidenchants.enchant.mechanism.EventType.*
import world.icebear03.splendidenchants.enchant.mechanism.Listeners
import world.icebear03.splendidenchants.enchant.mechanism.chain.ChainType.*
import world.icebear03.splendidenchants.enchant.mechanism.entry.event.Attack
import world.icebear03.splendidenchants.enchant.mechanism.entry.event.Interact
import world.icebear03.splendidenchants.enchant.mechanism.entry.event.Kill
import world.icebear03.splendidenchants.enchant.mechanism.entry.`object`.*
import world.icebear03.splendidenchants.enchant.mechanism.entry.operation.Plant
import world.icebear03.splendidenchants.enchant.mechanism.entry.operation.Println

class Chain(val listeners: Listeners, line: String) {

    val type = ChainType.getType(line.split("::")[0])
    val content = line.split("::")[1]

    //注意：这里的item一定要是原物品，不能是副本
    fun trigger(
        event: Event,
        eventType: EventType,
        entity: LivingEntity,
        item: ItemStack,
        holders: MutableMap<String, Any>
    ): Boolean {
        val variabled = content.replace(holders)
        val parts = variabled.split(":")

        val toPlayer = entity as? Player

        when (type) {
            //特殊条件：冷却，每个附魔只有一个冷却计数器
            //格式：
            //冷却::冷却时间(s):是否播报给玩家
            COOLDOWN -> {
                val cdInSec = parts[0].toDouble()
                val key = listeners.enchant.basicData.id
                val info = parts[1].toBoolean()

                val result = entity.checkCd(key, cdInSec)
                if (!result.first) {
                    if (info) entity.sendMessage("冷却未结束，还有${result.second}s")
                    return false
                }
                entity.addCd(key)
            }

            CONDITION -> {
                return if (parts.size > 1)
                    when (val tmp = holders[parts[0]]!!) {
                        is Player -> ObjectPlayer.modify(tmp, parts.subList(1), holders)
                        is LivingEntity -> ObjectLivingEntity.modify(tmp, parts.subList(1), holders)
                        is Entity -> ObjectEntity.modify(tmp, parts.subList(1), holders)
                        is Block -> ObjectBlock.modify(tmp, parts.subList(1), holders)
                        is ItemStack -> ObjectItem.modify(tmp, parts.subList(1), holders)
                        else -> false
                    }
                else variabled.calcToBoolean()
            }

            ASSIGNMENT -> {
                val variable = parts[0]
                val expression = parts[1]
                val value = expression.calculate()
                listeners.enchant.variable.modifyVariable(item, variable, value)
            }

            EVENT -> {
                when (eventType) {
                    KILL -> Kill.modify(event, entity, parts, holders)
                    ATTACK -> Attack.modify(event, entity, parts, holders)
                    RIGHT_CLICK, LEFT_CLICK, PHYSICAL_INTERACT -> Interact.modify(event, entity, parts, holders)
                    INTERACT_ENTITY -> {}
                }
            }

            OPERATION -> when (parts[0]) {
                "plant" -> submit submit@{ Plant.plant(toPlayer ?: return@submit, parts[1].toInt(), parts[2]) }
                "println" -> {
                    entity.sendMessage(holders.toString())
                    Println.println(entity, parts.joinToString(":"))
                }

                else -> {}
            }


            null -> {}
        }
        return true
    }
}
