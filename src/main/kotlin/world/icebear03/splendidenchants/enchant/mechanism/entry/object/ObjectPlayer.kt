package world.icebear03.splendidenchants.enchant.mechanism.entry.`object`

import org.bukkit.entity.Player
import world.icebear03.splendidenchants.api.calcToInt
import world.icebear03.splendidenchants.api.replace
import world.icebear03.splendidenchants.api.subList
import world.icebear03.splendidenchants.api.takeItem

object ObjectPlayer {

    fun modify(
        player: Player,
        params: List<String>, holders: MutableMap<String, Any>
    ): Boolean {
        if (ObjectLivingEntity.modify(player, params, holders)) return true

        val variabled = params.map { it.replace(holders) }
        val type = variabled[0]
        val after = variabled.subList(1)

        when (type) {
            "扣除物品" -> return player.takeItem(after.getOrNull(1)?.calcToInt() ?: 1) {
                it.type == holders[after[0]]!!
            }

            "TODO" -> {}

            else -> return false
        }
        return true
    }
}