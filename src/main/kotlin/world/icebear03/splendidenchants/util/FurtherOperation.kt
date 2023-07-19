package world.icebear03.splendidenchants.util

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import taboolib.common.platform.function.submit
import java.util.*

object FurtherOperation {

    // 游戏刻时间戳 to 是否已经处理过了
    //伤害者
    val lastDamageTracker = mutableMapOf<Pair<UUID, UUID>, Int>()
    val lastBreakTracker = mutableMapOf<Pair<Location, UUID>, Int>()

    init {
        submit(delay = 0L, period = 20L) {
            val tick = Bukkit.getCurrentTick()
            lastDamageTracker.filter {
                tick - it.value >= 20
            }.forEach {
                lastDamageTracker.remove(it.key)
            }
            lastBreakTracker.filter {
                tick - it.value >= 20
            }.forEach {
                lastBreakTracker.remove(it.key)
            }
        }
    }

    fun addStamp(event: Event) {
        if (event is EntityDamageByEntityEvent) {
            val key = event.damager.uniqueId to event.entity.uniqueId
            lastDamageTracker[key] = Bukkit.getCurrentTick()
        }
        if (event is BlockBreakEvent) {
            val key = event.block.location to event.player.uniqueId
            lastBreakTracker[key] = Bukkit.getCurrentTick()
        }
    }

    fun delStamp(event: Event) {
        if (event is EntityDamageByEntityEvent) {
            val key = event.damager.uniqueId to event.entity.uniqueId
            lastDamageTracker.remove(key)
        }
        if (event is BlockBreakEvent) {
            val key = event.block.location to event.player.uniqueId
            lastBreakTracker.remove(key)
        }
    }

    fun hadRun(event: Event): Boolean {
        if (event is EntityDamageByEntityEvent) {
            val key = event.damager.uniqueId to event.entity.uniqueId
            if (lastDamageTracker.containsKey(key)) {
                val tick = lastDamageTracker[key]!!
                if (tick >= Bukkit.getCurrentTick() - 1) {
                    return true
                }
            }
        }
        if (event is BlockBreakEvent) {
            val key = event.block.location to event.player.uniqueId
            if (lastBreakTracker.containsKey(key)) {
                val tick = lastBreakTracker[key]!!
                if (tick >= Bukkit.getCurrentTick() - 1) {
                    return true
                }
            }
        }
        return false
    }

    //尝试追加新的伤害，但是不迭代引发伤害监听器
    fun furtherDamage(damager: Entity, damaged: LivingEntity, damage: Double) {
        if (PermissionChecker.hasDamagePermission(damager, damaged)) {
            damaged.damage(damage, damager)
        }
    }

    //后面两个参数因附魔而异
    //比如较大面积的一次性破坏方块，triggereffects应该为false，否则会导致玩家卡顿
    fun furtherBreak(player: Player, block: Block, triggerEffects: Boolean = true, dropExperience: Boolean = true) {
        if (PermissionChecker.hasBlockPermission(player, block)) {
            block.breakNaturally(player.inventory.itemInMainHand, triggerEffects, dropExperience)
        }
    }
}