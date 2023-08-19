package world.icebear03.splendidenchants.enchant.mechanism.entry.event

import org.bukkit.entity.LivingEntity
import org.bukkit.event.Event
import org.bukkit.event.player.PlayerInteractEvent
import world.icebear03.splendidenchants.api.replace
import world.icebear03.splendidenchants.api.subList
import world.icebear03.splendidenchants.enchant.mechanism.entry.`object`.ObjectBlock
import world.icebear03.splendidenchants.enchant.mechanism.entry.`object`.ObjectItem
import world.icebear03.splendidenchants.enchant.mechanism.entry.`object`.ObjectPlayer

object Interact {

    fun modify(e: Event, entity: LivingEntity, params: List<String>, holders: MutableMap<String, Any>) {
        val event = e as PlayerInteractEvent

        event.run {
            holders["动作"] = action
            clickedBlock?.let { holders["交互方块"] = it }
            item?.let { holders["手持物品"] = it }
            holders["玩家"] = player
        }

        val variabled = params.map { it.replace(holders) }

        when (variabled[0]) {
            "取消交互", "取消" -> event.isCancelled = true
            "交互方块" -> event.clickedBlock?.let { ObjectBlock.modify(it, variabled.subList(1), holders) }
            "手持物品" -> event.item?.let { ObjectItem.modify(it, variabled.subList(1), holders) }
            "玩家" -> ObjectPlayer.modify(event.player, variabled.subList(1), holders)
            else -> {}
        }
    }
}