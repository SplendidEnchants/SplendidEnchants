package world.icebear03.splendidenchants.enchant.mechanism.entry.`object`

import org.bukkit.entity.Player
import world.icebear03.splendidenchants.api.calcToInt
import world.icebear03.splendidenchants.api.replace
import world.icebear03.splendidenchants.api.takeItem

object ObjectPlayer {

    fun modify(
        player: Player,
        params: List<String>, holders: MutableMap<String, Any>
    ): Boolean {
        if (ObjectLivingEntity.modify(player, params, holders)) return true

        val variabled = params.map { it.replace(holders) }

        when (variabled[0]) {
            "扣除物品" -> return player.takeItem(variabled.getOrNull(2)?.calcToInt() ?: 1) {
                it.type == holders[variabled[1]]!!
            }

            "TODO" -> {}

            else -> return false
        }
        return true
    }
}