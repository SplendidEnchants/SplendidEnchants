package world.icebear03.splendidenchants.api

import org.bukkit.FluidCollisionMode
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffectType
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
