package world.icebear03.splendidenchants.enchant.mechanism.entry.event

import org.bukkit.entity.LivingEntity
import org.bukkit.event.player.PlayerToggleSneakEvent
import world.icebear03.splendidenchants.enchant.mechanism.entry.internal.EventEntry
import world.icebear03.splendidenchants.enchant.mechanism.entry.internal.ObjectEntry
import world.icebear03.splendidenchants.enchant.mechanism.entry.internal.objPlayer
import world.icebear03.splendidenchants.enchant.mechanism.entry.internal.objString

object EventSneak : EventEntry<PlayerToggleSneakEvent>() {

    override fun modify(event: PlayerToggleSneakEvent, entity: LivingEntity, cmd: String, params: List<String>): Boolean {
        return true
    }

    override fun get(event: PlayerToggleSneakEvent, objName: String): Pair<ObjectEntry<*>, Any?> {
        return when (objName) {
            "下蹲者" -> objPlayer.holderize(event.player)
            else -> objString.holderize(null)
        }
    }
}