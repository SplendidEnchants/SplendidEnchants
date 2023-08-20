package world.icebear03.splendidenchants.enchant.mechanism.entry.event

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.player.PlayerToggleSneakEvent
import world.icebear03.splendidenchants.api.replace
import world.icebear03.splendidenchants.api.subList
import world.icebear03.splendidenchants.enchant.mechanism.entry.`object`.ObjectPlayer

object Sneak {
    fun modify(e: Event, entity: LivingEntity, params: List<String>, holders: MutableMap<String, Any>) {
        val event = e as PlayerToggleSneakEvent

        holders["下蹲者"] = entity

        val variabled = params.map { it.replace(holders) }
        val type = variabled[0]
        val after = variabled.subList(1)

        when (type) {
            "下蹲者" -> ObjectPlayer.modify(entity as Player, after, holders)
            else -> {}
        }
    }
}