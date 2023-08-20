package world.icebear03.splendidenchants.api

import org.bukkit.FluidCollisionMode
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffectType
import taboolib.module.nms.getI18nName
import taboolib.platform.util.countItem
import taboolib.platform.util.takeItem

fun Player.blockLookingAt(
    range: Double = 50.0,
    fluidCollisionMode: FluidCollisionMode = FluidCollisionMode.NEVER
) = rayTraceBlocks(range, fluidCollisionMode)?.hitBlock

fun LivingEntity.mainHand() = equipment?.itemInMainHand
fun LivingEntity.offHand() = equipment?.itemInOffHand

fun LivingEntity.effect(type: PotionEffectType, duration: Int, level: Int = 1) {
    this.addPotionEffect(type.createEffect(duration * 20, level - 1))
}

fun Player.takeItem(amount: Int = 1, matcher: (itemStack: ItemStack) -> Boolean): Boolean {
    if (inventory.countItem(matcher) >= amount) {
        inventory.takeItem(amount, matcher)
        return true
    }
    return false
}

fun LivingEntity.realDamage(amount: Double, who: Entity? = null) {
    health = maxOf(0.1, health - amount + 0.5)
    damage(0.5, who)
}

val Entity.displayName get() = (this as? Player)?.name ?: customName ?: getI18nName()

val LivingEntity.blockBelow
    get():Block? {
        val loc = location
        repeat(loc.blockY + 63) {
            val current = loc.clone()
            current.y -= it.toDouble() + 1
            if (current.block.type != Material.AIR) {
                return current.block
            }
        }
        return null
    }