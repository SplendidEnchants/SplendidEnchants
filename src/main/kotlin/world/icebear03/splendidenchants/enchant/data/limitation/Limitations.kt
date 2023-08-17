package world.icebear03.splendidenchants.enchant.data.limitation

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import taboolib.module.kether.compileToJexl
import taboolib.platform.compat.replacePlaceholder
import world.icebear03.splendidenchants.api.fixedEnchants
import world.icebear03.splendidenchants.api.splendidEt
import world.icebear03.splendidenchants.enchant.SplendidEnchant
import world.icebear03.splendidenchants.enchant.data.belongedTargets
import world.icebear03.splendidenchants.enchant.data.capability
import world.icebear03.splendidenchants.enchant.data.group
import world.icebear03.splendidenchants.enchant.data.isIn
import world.icebear03.splendidenchants.enchant.data.limitation.LimitType.*

class Limitations(
    private val belonging: SplendidEnchant,
    lines: List<String>
) {
    val limitations = listOf(MAX_CAPABILITY to "", TARGET to "", DISABLE_WORLD to "") +
            lines.map { LimitType.valueOf(it.split(":")[0]) to it.split(":")[1] }

    // 检查操作是否被允许（比如是否可以附魔到某个物品上、使用时是否可以生效、村民生成新交易等）
    // item 就是跟操作直接有关的物品（如正在被附魔的书、正在使用的剑、生成的新交易中卖出的附魔书等）
    fun checkAvailable(checkType: CheckType, item: ItemStack, creature: LivingEntity? = null, slot: EquipmentSlot? = null): Pair<Boolean, String> {
        if (!belonging.basicData.enable)
            return false to "附魔未启用"

        limitations.filter { checkType.has(it.first) }.forEach { (type, value) ->
            when (type) {
                PAPI_EXPRESSION ->
                    if (creature is Player) value.replacePlaceholder(creature).compileToJexl().eval() as Boolean
                    else true

                PERMISSION -> (creature?.hasPermission(value) ?: true)
                DISABLE_WORLD -> !belonging.basicData.disableWorlds.contains(creature?.world?.name)
                TARGET, MAX_CAPABILITY, SLOT,
                CONFLICT_ENCHANT, CONFLICT_GROUP,
                DEPENDENCE_ENCHANT, DEPENDENCE_GROUP -> checkItem(type, item, value, slot)
            }.run { if (!this) return false to "${type.typeName}:$value" }
        }

        return true to ""
    }

    private fun checkItem(type: LimitType, item: ItemStack, value: String, slot: EquipmentSlot? = null): Boolean {
        val itemType = item.type
        val enchants = item.fixedEnchants
        return when (type) {
            SLOT -> itemType.belongedTargets.any { it.activeSlots.contains(slot) }
            TARGET -> belonging.targets.any { itemType.isIn(it) }
            MAX_CAPABILITY -> itemType.capability > enchants.size
            DEPENDENCE_ENCHANT -> return enchants.containsKey(splendidEt(value))
            CONFLICT_ENCHANT -> return !enchants.containsKey(splendidEt(value))
            DEPENDENCE_GROUP -> enchants.any { (enchant, _) -> enchant.isIn(value) }
            CONFLICT_GROUP -> enchants.count { (enchant, _) -> enchant.isIn(value) } < (group(value)?.maxCoexist ?: 10000)
            else -> true
        }
    }
}
