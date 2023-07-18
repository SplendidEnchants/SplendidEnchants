package world.icebear03.splendidenchants.util

import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity

object FurtherOperation {

    //尝试追加新的伤害，但是不迭代引发伤害监听器
    fun furtherDamage(damager: Entity, damaged: LivingEntity, damage: Double) {
        if (PermissionChecker.hasDamagePermission(damager, damaged)) {
            damaged.damage(damage, damager)
        }
    }
}