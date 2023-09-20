package world.icebear03.splendidenchants.enchant.mechanism.entry.event

import org.bukkit.entity.LivingEntity
import org.bukkit.event.player.AsyncPlayerChatEvent
import world.icebear03.splendidenchants.enchant.mechanism.entry.internal.*

object EventChat : EventEntry<AsyncPlayerChatEvent>() {

    override fun modify(event: AsyncPlayerChatEvent, entity: LivingEntity, cmd: String, params: List<String>): Boolean {
        when (cmd) {
            "取消发送" -> event.isCancelled = true
            "设置信息" -> event.message = params.firstOrNull() ?: ""
            else -> {}
        }
        return true
    }

    override fun get(event: AsyncPlayerChatEvent, objName: String): Pair<ObjectEntry<*>, Any?> {
        return when (objName) {
            "消息" -> objString.h(event.message)
            "发送者" -> objPlayer.h(event.player)
            else -> EventChat[event, objName]
        }
    }
}
