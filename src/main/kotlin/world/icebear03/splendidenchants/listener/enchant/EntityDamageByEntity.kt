package world.icebear03.splendidenchants.listener.enchant

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

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun event(event: EntityShootBowEvent) {
        if (event.isCancelled)
            return

        if (event.bow == null)
            return

        projectileSourceItems[event.projectile.uniqueId] = event.bow!!
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun event(event: EntityRemoveFromWorldEvent) {
        projectileSourceItems.remove(event.entity.uniqueId)
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun event(event: EntityDamageByEntityEvent) {
        if (event.isCancelled)
            return

        val damageEntity = event.damager
        val damagedEntity = event.entity
        var isProjectile = false

        if (damagedEntity is ArmorStand || damagedEntity !is LivingEntity)
            return

        val damagee = damagedEntity
        val damager: Player = if (damageEntity is Projectile) {
            if (damageEntity.shooter !is Player)
                return
            isProjectile = true
            damageEntity.shooter as Player
        } else damageEntity as Player

        var weapon = damager.inventory.itemInMainHand
        if (isProjectile) {
            if (damageEntity is Arrow) {
                weapon = projectileSourceItems[damageEntity.uniqueId]!!
            }
            if (damageEntity is Trident) {
                weapon = damageEntity.item
            }
        }

        //以下是测试代码

        ItemAPI.getEnchants(weapon).forEach {
            it.key.listeners.trigger(event, EventType.ATTACK, org.bukkit.event.EventPriority.HIGHEST, damager, weapon)
        }

        submit {
            if (damagee.isDead) {
                println("dead")

                ItemAPI.getEnchants(weapon).forEach {
                    it.key.listeners.trigger(
                        event,
                        EventType.KILL,
                        org.bukkit.event.EventPriority.HIGHEST,
                        damager,
                        weapon
                    )
                }
            }
        }
    }
}