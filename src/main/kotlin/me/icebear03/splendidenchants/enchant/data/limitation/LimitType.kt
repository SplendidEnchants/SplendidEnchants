package me.icebear03.splendidenchants.enchant.data.limitation

/**
 * SplendidEnchants
 * me.icebear03.splendidenchants.enchant.data.limitation.LimitType
 *
 * @author mical
 * @since 2023/6/19 7:32 PM
 */
enum class LimitType(val typeName: String) {

    // TODO: 应当自定义语言
    PAPI_EXPRESSION("表达式"), // 如 %player_level%>=30
    PERMISSION("权限"), // 如 splendidenchants.use
    CONFLICT("冲突"), // 如 锋利
    DEPENDENCE("依赖"); // 如 无限
}