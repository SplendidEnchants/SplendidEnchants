package world.icebear03.splendidenchants.listener.mechanism

import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerExpChangeEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.console
import taboolib.common.platform.function.submit
import world.icebear03.splendidenchants.api.calcToInt
import world.icebear03.splendidenchants.api.internal.YamlUpdater


object ExpListener {

    var enable: Boolean = false
    val expFormulas = mutableListOf<Pair<Int, String>>()
    val privilege = mutableMapOf<String, String>()

    fun load() {
        YamlUpdater.loadAndUpdate("mechanisms/exp.yml").run {
            enable = getBoolean("enable", false)

            expFormulas.clear()
            getConfigurationSection("exp_per_level")?.let {
                expFormulas.addAll(it.getKeys(false).map { path -> path.toInt() to it.getString(path)!! })
            }

            privilege.clear()
            privilege.putAll(getStringList("privilege").map { it.split(":")[0] to it.split(":")[1] })
        }

        console().sendMessage("    Successfully load exp module")
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onExp(event: PlayerExpChangeEvent) {
        if (!enable)
            return

        val player = event.player
        val level = player.level //currentLevel
        val attained = finalAttain(event.amount, player)

        val percent = player.exp

        val expNeedToUpgrade = modified(level)
        val exp = percent * expNeedToUpgrade
        val newExp = exp + attained

        event.amount = 0

        submit {
            player.exp = if (newExp >= expNeedToUpgrade) {
                player.level += 1
                (newExp - expNeedToUpgrade) / modified(level + 1)
            } else newExp / expNeedToUpgrade
        }
    }

    fun modified(level: Int) = expFormulas.lastOrNull { it.first <= level }?.second?.calcToInt("level" to level) ?: vanilla(level)

    fun vanilla(level: Int) = if (level <= 15) 2 * level + 7
    else if (level <= 30) 5 * level - 38
    else 9 * level - 158

    fun finalAttain(origin: Int, player: Player) = privilege.maxOf { (perm, expression) ->
        if (player.hasPermission(perm)) expression.calcToInt("exp" to origin)
        else origin
    }.coerceAtLeast(0)
}