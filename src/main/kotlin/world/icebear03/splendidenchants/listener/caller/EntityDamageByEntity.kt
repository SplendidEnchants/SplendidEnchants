package world.icebear03.splendidenchants.listener.caller

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent
import org.bukkit.entity.*
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import world.icebear03.splendidenchants.api.ItemAPI
import world.icebear03.splendidenchants.api.internal.FurtherOperation
import world.icebear03.splendidenchants.api.internal.PermissionChecker
import world.icebear03.splendidenchants.enchant.mechanism.EventType
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object EntityDamageByEntity {

    val projectileSourceItems = ConcurrentHashMap<UUID, ItemStack>()

    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun event(event: EntityShootBowEvent) {
        if (event.isCancelled)
            return

        if (event.bow == null)
            return

        projectileSourceItems[event.projectile.uniqueId] = event.bow!!
    }

    @SubscribeEvent(priority = EventPriority.MONITOR)
    fun event(event: EntityRemoveFromWorldEvent) {
        projectileSourceItems.remove(event.entity.uniqueId)
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun eventLowest(event: EntityDamageByEntityEvent) {
        settle(event, EventPriority.LOWEST)
    }

    @SubscribeEvent(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun eventHigh(event: EntityDamageByEntityEvent) {
        settle(event, EventPriority.HIGH)
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun eventHighest(event: EntityDamageByEntityEvent) {
        settle(event, EventPriority.HIGHEST)
        //HIGHEST处必带
    }

    private fun settle(event: EntityDamageByEntityEvent, priority: EventPriority) {
//        println("${System.currentTimeMillis()}正在处理攻击 $priority")

        //伤害生物、破坏方块的settle必带
        if (PermissionChecker.isChecking(event)) {
//            println("${System.currentTimeMillis()}权限检查攻击，取消 $priority")
            return
        }
        if (FurtherOperation.hadOperated(event)) {
//            println("${System.currentTimeMillis()}已经处理的攻击，取消 $priority")
            return
        }

//        println("${System.currentTimeMillis()}这是未处理的攻击，添加时间戳 $priority")
        FurtherOperation.addStamp(event)
        //-------------------------

        val damageEntity = event.damager
        val damagedEntity = event.entity
        var isProjectile = false

        if (damagedEntity is ArmorStand || damagedEntity !is LivingEntity)
            return

        val damaged = damagedEntity
        val damager: Player = if (damageEntity is Projectile) {
            if (damageEntity.shooter !is Player)
                return
            isProjectile = true
            damageEntity.shooter as Player
        } else {
            if (damageEntity !is Player)
                return
            damageEntity
        }

        var weapon = damager.inventory.itemInMainHand
        if (isProjectile) {
            if (damageEntity is Arrow) {
                weapon = projectileSourceItems[damageEntity.uniqueId]!!
            }
            if (damageEntity is Trident) {
                weapon = damageEntity.item
            }
        }

//        println("攻击，-----------------------------------------伤害")
        ItemAPI.getEnchants(weapon).forEach {
            it.key.listeners.trigger(event, EventType.ATTACK, priority, damager, weapon)
        }

        //必带
        FurtherOperation.delStamp(event)
//        println("${System.currentTimeMillis()}攻击已经处理，去除时间戳 $priority")

        submit {
            if (damaged.isDead) {
                ItemAPI.getEnchants(weapon).forEach {
                    it.key.listeners.trigger(
                        event,
                        EventType.KILL,
                        priority,
                        damager,
                        weapon
                    )
                }
            }
        }
    }
}