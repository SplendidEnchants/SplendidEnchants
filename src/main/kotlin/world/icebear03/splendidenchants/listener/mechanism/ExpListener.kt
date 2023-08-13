package world.icebear03.splendidenchants.listener.mechanism

import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerExpChangeEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.console
import taboolib.common.platform.function.submit
import taboolib.module.kether.compileToJexl
import world.icebear03.splendidenchants.api.internal.YamlUpdater
import kotlin.math.roundToInt


object ExpListener {

    var enable: Boolean = false
    val expFormula = mutableMapOf<Int, String>()
    val privilege = mutableMapOf<String, String>()

    fun initialize() {
        val config = YamlUpdater.loadAndUpdate("mechanisms/exp.yml")

        enable = config.getBoolean("enable", false)!!

        expFormula.clear()
        val section = config.getConfigurationSection("exp_per_level")!!
        section.getKeys(false).forEach {
            expFormula[it.toInt()] = section.getString("$it")!!
        }

        privilege.clear()
        config.getStringList("privilege").forEach { it ->
            privilege[it.split(":")[0]] = it.split(":")[1]
        }

        console().sendMessage("    Successfully load exp module")
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onExp(event: PlayerExpChangeEvent) {
        if (!enable)
            return

        val player = event.player
        val level = player.level //currentLevel
        val expAttained = finalAttain(event.amount.toFloat(), player)

        val percent = player.exp

        val expNeedToUpgrade = getExpPerLevel(level)
        val exp = percent * expNeedToUpgrade

        event.amount = 0

        submit {
            val newExp = exp + expAttained
            if (newExp >= expNeedToUpgrade) {
                player.level = level + 1
                val leftExp = newExp - expNeedToUpgrade
                player.exp = leftExp / getExpPerLevel(level + 1)
            } else {
                player.exp = newExp / expNeedToUpgrade
            }
        }
    }

    fun getExpPerLevel(level: Int): Int {
        var tmp = -1
        var formula = ""
        expFormula.forEach {
            if (it.key in (tmp + 1)..level) {
                tmp = it.key
                formula = it.value
            }
        }
        if (formula.isEmpty())
            return getExpPerLevelInVanilla(level)
        else
            return formula.replace("{level}", "$level").compileToJexl().eval() as Int
    }

    fun getExpPerLevelInVanilla(level: Int): Int {
        return if (level <= 15) {
            2 * level + 7
        } else if (level <= 30) {
            5 * level - 38
        } else {
            9 * level - 158
        }
    }

    fun finalAttain(origin: Float, player: Player): Int {
        var maxAttain = origin
        privilege.forEach {
            if (player.hasPermission(it.key)) {
                val newRefund = it.value.replace("{exp}", origin.toString()).compileToJexl().eval() as Float
                maxAttain = maxOf(maxAttain, newRefund)
            }
        }

        return maxOf(0, maxAttain.roundToInt())
    }
}