package me.icebear03.splendidenchants.enchant.data.limitation

enum class CheckType(vararg types: LimitType) {

    //TODO 应当改为可配置 就像2.7那样
    ATTAIN(
        LimitType.PAPI_EXPRESSION,
        LimitType.PERMISSION,
        LimitType.CONFLICT_GROUP,
        LimitType.CONFLICT_ENCHANT,
        LimitType.DEPENDENCE_GROUP,
        LimitType.DEPENDENCE_ENCHANT
    ), // 从战利品/附魔台中获得附魔物品时
    MERCHANT(
        LimitType.PAPI_EXPRESSION,
        LimitType.PERMISSION,
        LimitType.CONFLICT_GROUP,
        LimitType.CONFLICT_ENCHANT,
        LimitType.DEPENDENCE_GROUP,
        LimitType.DEPENDENCE_ENCHANT
    ), // 生成村民交易中的附魔时
    ANVIL(
        LimitType.CONFLICT_GROUP,
        LimitType.CONFLICT_ENCHANT,
        LimitType.DEPENDENCE_GROUP,
        LimitType.DEPENDENCE_ENCHANT
    ), // 进行铁砧拼合物品附魔时
    USE(
        LimitType.CONFLICT_GROUP,
        LimitType.CONFLICT_ENCHANT,
        LimitType.DEPENDENCE_GROUP,
        LimitType.DEPENDENCE_ENCHANT
    ); // 使用物品上的附魔时

    val limitTypes = hashSetOf<LimitType>()

    init {
        limitTypes.addAll(arrayListOf(*types))
    }

    fun containsType(limitType: LimitType): Boolean = limitTypes.contains(limitType)
}