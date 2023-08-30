package world.icebear03.splendidenchants.enchant.mechanism.entry.event

import org.bukkit.entity.LivingEntity
import org.bukkit.event.player.PlayerToggleFlightEvent
import world.icebear03.splendidenchants.enchant.mechanism.entry.internal.EventEntry
import world.icebear03.splendidenchants.enchant.mechanism.entry.internal.ObjectEntry
import world.icebear03.splendidenchants.enchant.mechanism.entry.internal.objPlayer
import world.icebear03.splendidenchants.enchant.mechanism.entry.internal.objString

object EventFly : EventEntry<PlayerToggleFlightEvent>() {

    override fun modify(event: PlayerToggleFlightEvent, entity: LivingEntity, cmd: String, params: List<String>): Boolean {
        return true
    }

    override fun get(event: PlayerToggleFlightEvent, objName: String): Pair<ObjectEntry<*>, Any?> {
        return when (objName) {
            "飞行者" -> objPlayer.holderize(event.player)
            else -> objString.h(null)
        }
    }
}