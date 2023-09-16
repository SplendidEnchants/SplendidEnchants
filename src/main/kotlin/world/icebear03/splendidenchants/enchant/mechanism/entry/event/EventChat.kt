package world.icebear03.splendidenchants.enchant.mechanism.entry.event

import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.entity.LivingEntity
import world.icebear03.splendidenchants.enchant.mechanism.entry.internal.*

object EventChat : EventEntry<AsyncChatEvent>() {

    override fun modify(event: AsyncChatEvent, entity: LivingEntity, cmd: String, params: List<String>): Boolean {
        when (cmd) {
            "取消发送" -> event.isCancelled = true
            else -> {}
        }
        return true
    }

    override fun get(event: AsyncChatEvent, objName: String): Pair<ObjectEntry<*>, Any?> {
        return when (objName) {
            "消息" -> objString.h(event.message())
            "发送者" -> objPlayer.h(event.player)
            else -> EventChat[event, objName]
        }
    }
}