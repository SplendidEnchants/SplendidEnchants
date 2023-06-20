package me.icebear03.splendidenchants.enchant.data.limitation

import me.icebear03.splendidenchants.api.EnchantAPI
import me.icebear03.splendidenchants.api.ItemAPI
import me.icebear03.splendidenchants.enchant.EnchantGroup
import me.icebear03.splendidenchants.enchant.data.limitation.LimitType.*
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack

class Limitations(vararg lines: String) {

    var limitations = arrayListOf<Pair<LimitType, String>>()

    init {
        lines.forEach { limitations.add(LimitType.valueOf(it.split(":")[0]) to it.split(":")[1]) }
    }

    // 检查操作是否被允许（比如是否可以附魔到某个物品上、使用时是否可以生效、村民生成新交易等）
    // item 就是跟操作直接有关的物品（如正在被附魔的书、正在使用的剑、生成的新交易中卖出的附魔书等）
    fun checkAvailable(checkType: CheckType, creature: LivingEntity, item: ItemStack): Pair<Boolean, String> {
        for (limitation in limitations) {
            if (!checkType.containsType(limitation.first))
                continue

            val limitType = limitation.first
            val value = limitation.second
            when (limitType) {
                PAPI_EXPRESSION ->
                    //TODO papi 表达式的处理
                    //TODO 返回信息记得 replace 中文，比如 %player_level%>=30 应当翻译为 "经验等级>=30"
                    return false to "{limit.papi_expression}"

                PERMISSION -> {
                    if (!creature.hasPermission(value))
                        return false to "{limit.permission} || permission=$value"
                }

                CONFLICT_ENCHANT, DEPENDENCE_ENCHANT, CONFLICT_GROUP, DEPENDENCE_GROUP -> {
                    var result: Pair<Boolean, String> = checkAvailable(item)
                    if (!result.first)
                        return result
                }
            }
        }
        return true to ""
    }

    fun checkAvailable(item: ItemStack): Pair<Boolean, String> {
        for (limitation in limitations) {
            val limitType = limitation.first
            val value = limitation.second
            when (limitType) {
                CONFLICT_ENCHANT -> {
                    if (ItemAPI.containsEnchant(item, EnchantAPI.getSplendidEnchant(value)))
                        return false to "{limit.conflict} || enchant=$value"
                }

                DEPENDENCE_ENCHANT -> {
                    if (!ItemAPI.containsEnchant(item, EnchantAPI.getSplendidEnchant(value)))
                        return false to "{limit.conflict} || enchant=$value"
                }

                CONFLICT_GROUP -> {
                    ItemAPI.getEnchants(item).keys.forEach { enchant ->
                        if (EnchantGroup.isIn(enchant, value))
                            return false to "{limit.conflict} || enchant=$value"
                    }
                }

                DEPENDENCE_GROUP -> {
                    var flag: Boolean = false
                    ItemAPI.getEnchants(item).keys.forEach { enchant ->
                        if (EnchantGroup.isIn(enchant, value))
                            flag = true
                    }
                    if (!flag)
                        return false to "{limit.dependence} || enchant=$value"
                }

                PAPI_EXPRESSION, PERMISSION -> {}
            }
        }
        return true to ""
    }

    fun conflictWith(enchant: Enchantment): Boolean {
        for (limitation in limitations) {
            val limitType = limitation.first
            val value = limitation.second
            if (limitType == CONFLICT_GROUP) {
                if (value == EnchantAPI.getName(enchant))
                    return true
            }
            if (limitType == CONFLICT_ENCHANT) {
                if (EnchantGroup.isIn(enchant, value))
                    return true
            }
        }
        return false
    }

    fun addLimitation(limitType: LimitType, value: String) {
        limitations.add(limitType to value)
    }
}
