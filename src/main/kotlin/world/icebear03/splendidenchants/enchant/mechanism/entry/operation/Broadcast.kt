package world.icebear03.splendidenchants.enchant.mechanism.entry.operation

import taboolib.platform.util.onlinePlayers
import taboolib.platform.util.sendLang
import world.icebear03.splendidenchants.api.translate

object Broadcast {
    fun broadcast(text: String) {
        println(text.translate())
        onlinePlayers.forEach { it.sendLang(text) }
    }
}