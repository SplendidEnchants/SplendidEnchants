package world.icebear03.splendidenchants.util

import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

object FurtherOperation {

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