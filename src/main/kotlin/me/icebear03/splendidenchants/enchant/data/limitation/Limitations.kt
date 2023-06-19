package me.icebear03.splendidenchants.enchant.data.limitation

import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack

/**
 * SplendidEnchants
 * me.icebear03.splendidenchants.enchant.data.limitation.Limitations
 *
 * @author mical
 * @since 2023/6/19 7:39 PM
 */
class Limitations(vararg lines: String) {

    var limitations = arrayListOf<Pair<LimitType, String>>()

    init {
        lines.forEach { limitations.add(LimitType.valueOf(it.split(":")[0]) to it.split(":")[1]) }
    }

    // 检查操作是否被允许（比如是否可以附魔到某个物品上、使用时是否可以生效、村民生成新交易等）
    // item 就是跟操作直接有关的物品（如正在被附魔的书、正在使用的剑、生成的新交易中卖出的附魔书等）
    fun checkAvaliable(checkType: CheckType, creature: LivingEntity, item: ItemStack?): Pair<Boolean, String> {
        for (limitation in limitations) {
            val limitType = limitation.first
            val value = limitation.second
            return when (limitType) {
                LimitType.PAPI_EXPRESSION ->
                    //TODO papi 表达式的处理
                    //TODO 返回信息记得 replace 中文，比如 %player_level%>=30 应当翻译为 "经验等级>=30"
                    false to "{limit.papi_expression}"

                LimitType.PERMISSION -> {
                    if (!creature.hasPermission(value))
                        false to "{limit.permission} || permission=$value"
                    else false to "{limit.conflict} || enchant=$value"
                }

                LimitType.CONFLICT -> false to "{limit.conflict} || enchant=$value"

                LimitType.DEPENDENCE -> false to "{limit.dependence} || enchant=$value"
            }
        }
        return true to ""
    }

    // TODO: 添加 limitation
    // TODO: 根据 limitation生成附魔介绍（GUI（附属中）、文字输出等）
}
