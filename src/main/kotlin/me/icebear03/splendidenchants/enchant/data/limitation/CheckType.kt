package me.icebear03.splendidenchants.enchant.data.limitation

import java.util.*

/**
 * SplendidEnchants
 * me.icebear03.splendidenchants.enchant.data.limitation.CheckType
 *
 * @author mical
 * @since 2023/6/19 7:35 PM
 */
enum class CheckType(vararg types: LimitType) {

    //TODO 应当改为可配置 就像2.7那样
    ATTAIN(
        LimitType.PAPI_EXPRESSION,
        LimitType.PERMISSION,
        LimitType.CONFLICT,
        LimitType.DEPENDENCE
    ), // 从战利品/附魔台中获得附魔物品时
    MERCHANT(LimitType.PAPI_EXPRESSION, LimitType.PERMISSION, LimitType.CONFLICT, LimitType.DEPENDENCE), // 生成村民交易中的附魔时
    ANVIL(LimitType.CONFLICT, LimitType.DEPENDENCE), // 进行铁砧拼合物品附魔时
    USE(LimitType.CONFLICT, LimitType.DEPENDENCE); // 使用物品上的附魔时

    val limitTypes = hashSetOf<LimitType>()

    init {
        limitTypes.addAll(arrayListOf(*types))
    }
}