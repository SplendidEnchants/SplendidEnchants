package world.icebear03.splendidenchants.enchant.data.limitation

enum class CheckType(vararg types: LimitType) {
    ATTAIN(
        LimitType.PAPI_EXPRESSION,
        LimitType.PERMISSION,
        LimitType.CONFLICT_GROUP,
        LimitType.CONFLICT_ENCHANT,
        LimitType.DEPENDENCE_GROUP,
        LimitType.DEPENDENCE_ENCHANT,
        LimitType.MAX_CAPABILITY,
        LimitType.TARGET,
    ), // 从战利品/附魔台中获得附魔物品时
    MERCHANT(
        LimitType.CONFLICT_GROUP,
        LimitType.CONFLICT_ENCHANT,
        LimitType.DEPENDENCE_GROUP,
        LimitType.DEPENDENCE_ENCHANT,
        LimitType.MAX_CAPABILITY,
        LimitType.TARGET
    ), // 生成村民交易中的附魔时
    ANVIL(
        LimitType.CONFLICT_GROUP,
        LimitType.CONFLICT_ENCHANT,
        LimitType.DEPENDENCE_GROUP,
        LimitType.DEPENDENCE_ENCHANT,
        LimitType.MAX_CAPABILITY,
        LimitType.TARGET
    ), // 进行铁砧拼合物品附魔时
    USE(
        LimitType.PAPI_EXPRESSION,
        LimitType.DISABLE_WORLD,
        LimitType.TARGET,
        LimitType.SLOT
    ); // 使用物品上的附魔时

    val limitTypes = mutableSetOf(*types)

    fun has(limitType: LimitType): Boolean = limitTypes.contains(limitType)
}