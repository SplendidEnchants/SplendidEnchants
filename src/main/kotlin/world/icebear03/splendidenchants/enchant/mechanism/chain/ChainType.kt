package world.icebear03.splendidenchants.enchant.mechanism.chain

enum class ChainType {
    COOLDOWN,
    CONDITION,
    ASSIGNMENT,
    EVENT,
    OPERATION;

    companion object {
        fun fromString(string: String): ChainType {
            return when (string) {
                "冷却" -> COOLDOWN
                "条件" -> CONDITION
                "赋值" -> ASSIGNMENT
                "事件" -> EVENT
                "操作" -> OPERATION
                else -> valueOf(string)
            }
        }
    }
}