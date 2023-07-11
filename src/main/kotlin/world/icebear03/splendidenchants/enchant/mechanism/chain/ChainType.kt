package world.icebear03.splendidenchants.enchant.mechanism.chain

enum class ChainType {
    CONDITION,
    ASSIGNMENT,
    EVENT,
    OPERATION;

    companion object {
        fun fromString(string: String): ChainType {
            //TODO I18n
            return when (string) {
                "条件" -> CONDITION
                "赋值" -> ASSIGNMENT
                "事件" -> EVENT
                "操作" -> OPERATION
                else -> valueOf(string)
            }
        }
    }
}