package world.icebear03.splendidenchants.enchant.data.limitation

enum class LimitType(val typeName: String) {
    TARGET("附魔对象"), // 如 锋利只能应用于斧、剑
    MAX_CAPABILITY("词条个数"), // 如 一把剑最多有12附魔（默认）
    PAPI_EXPRESSION("表达式"), // 如 %player_level%>=30
    PERMISSION("权限"), // 如 splendidenchants.use
    CONFLICT_ENCHANT("冲突"), // 如 锋利
    CONFLICT_GROUP("冲突"), // 如 "PVE类附魔"
    DEPENDENCE_ENCHANT("依赖"), // 如 无限
    DEPENDENCE_GROUP("依赖"), // 如 "保护类附魔"
    DISABLE_WORLD("禁用世界"), // 如 world_the_end
    SLOT("可用位置"); // 如 HAND
}