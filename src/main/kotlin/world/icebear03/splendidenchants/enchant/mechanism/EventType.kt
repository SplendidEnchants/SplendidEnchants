package world.icebear03.splendidenchants.enchant.mechanism

import world.icebear03.splendidenchants.enchant.mechanism.entry.event.*
import world.icebear03.splendidenchants.enchant.mechanism.entry.internal.EventEntry

enum class EventType(val display: String, val entry: EventEntry<*>) {
    ATTACK("攻击", EventAttack),
    KILL("击杀", EventKill),
    RIGHT_CLICK("右击", EventInteract), //包含交互生物
    LEFT_CLICK("左击", EventInteract),
    INTERACT_ENTITY("交互生物", EventInteractEntity),
    PHYSICAL_INTERACT("交互方块", EventInteract),
    DAMAGED("受伤", EventDamaged),
    SNEAK("下蹲", EventSneak);

    companion object {
        fun getType(identifier: String?): EventType? = entries.find { it.display == identifier || it.name == identifier }
    }
}