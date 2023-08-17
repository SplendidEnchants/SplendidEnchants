package world.icebear03.splendidenchants.listener.caller

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent
import org.bukkit.entity.*
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.platform.util.attacker
import world.icebear03.splendidenchants.api.fixedEnchants
import world.icebear03.splendidenchants.api.internal.FurtherOperation
import world.icebear03.splendidenchants.api.internal.PermissionChecker
import world.icebear03.splendidenchants.api.mainHand
import world.icebear03.splendidenchants.enchant.mechanism.EventType
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object EntityDamageByEntity {

    val projectileSourceItems = ConcurrentHashMap<UUID, ItemStack>()

    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun shoot(event: EntityShootBowEvent) {
        val handItem = event.entity.equipment?.getItem(event.hand)
        projectileSourceItems[event.projectile.uniqueId] = event.bow ?: handItem ?: return
    }

    @SubscribeEvent(priority = EventPriority.MONITOR)
    fun remove(event: EntityRemoveFromWorldEvent) = projectileSourceItems.remove(event.entity.uniqueId)

    @SubscribeEvent(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun lowest(event: EntityDamageByEntityEvent) = settle(event, EventPriority.LOWEST)

    @SubscribeEvent(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun high(event: EntityDamageByEntityEvent) = settle(event, EventPriority.HIGH)

    @SubscribeEvent(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun highest(event: EntityDamageByEntityEvent) = settle(event, EventPriority.HIGHEST)

    private fun settle(event: EntityDamageByEntityEvent, priority: EventPriority) {
        if (PermissionChecker.isChecking(event)) return
        if (FurtherOperation.hadOperated(event)) return
        FurtherOperation.addStamp(event)

        val attacker = event.attacker ?: return
        val damager = event.damager
        val damaged = event.entity
        val isProjectile = damager is Projectile

        if (damaged is ArmorStand || damaged !is LivingEntity) return

        val weapon =
            if (isProjectile)
                if (damager is Trident) damager.item
                else projectileSourceItems[damager.uniqueId]
            else (attacker.mainHand() ?: return)


        weapon ?: return

        weapon.fixedEnchants.forEach { (enchant, _) ->
            enchant.listeners.trigger(event, EventType.ATTACK, priority, attacker, weapon)
        }

        FurtherOperation.delStamp(event)

        submit {
            if (damaged.isDead) {
                weapon.fixedEnchants.forEach { (enchant, _) ->
                    enchant.listeners.trigger(event, EventType.KILL, priority, attacker, weapon)
                }
            }
        }
    }
}