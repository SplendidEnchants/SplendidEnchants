package me.icebear03.splendidenchants.enchant.data.limitation

enum class LimitType(val typeName: String) {

    // TODO: 应当自定义语言
    PAPI_EXPRESSION("表达式"), // 如 %player_level%>=30
    PERMISSION("权限"), // 如 splendidenchants.use
    CONFLICT_ENCHANT("冲突"), // 如 锋利
    CONFLICT_GROUP("冲突"), // 如 "PVE类附魔"
    DEPENDENCE_ENCHANT("依赖"), // 如 无限
    DEPENDENCE_GROUP("依赖"); // 如 "保护类附魔"
}