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
    }

    private fun settle(event: EntityDamageByEntityEvent, priority: EventPriority) {
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

        ItemAPI.getEnchants(weapon).forEach {
            it.key.listeners.trigger(event, EventType.ATTACK, priority, damager, weapon)
        }

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