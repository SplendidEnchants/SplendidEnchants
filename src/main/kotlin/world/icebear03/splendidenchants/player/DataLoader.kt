package world.icebear03.splendidenchants.player

import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.platform.util.onlinePlayers

object DataLoader {

    fun load() {
        onlinePlayers.forEach { it.loadSEData() }
        submit(period = 600L) {
            onlinePlayers.forEach { it.saveSEData() }
        }
    }

    @SubscribeEvent(priority = EventPriority.MONITOR)
    fun join(event: PlayerJoinEvent) {
        event.player.loadSEData()
    }

    @SubscribeEvent(priority = EventPriority.MONITOR)
    fun quit(event: PlayerQuitEvent) {
        event.player.saveSEData()
    }
}