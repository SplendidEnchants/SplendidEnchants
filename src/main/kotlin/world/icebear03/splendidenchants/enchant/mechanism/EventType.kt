package world.icebear03.splendidenchants.enchant.mechanism

enum class EventType(val display: String) {
    ATTACK("攻击"),
    KILL("击杀"),
    RIGHT_CLICK("右击"),
    LEFT_CLICK("左击"),
    INTERACT_ENTITY("交互");

    companion object {
        fun getType(identifier: String?): EventType? = entries.find { it.display == identifier || it.name == identifier }
    }
}