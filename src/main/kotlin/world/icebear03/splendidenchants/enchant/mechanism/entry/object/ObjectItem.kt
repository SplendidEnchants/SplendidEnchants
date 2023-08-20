package world.icebear03.splendidenchants.enchant.mechanism.entry.`object`

import org.bukkit.inventory.ItemStack
import world.icebear03.splendidenchants.api.replace
import world.icebear03.splendidenchants.api.subList

object ObjectItem {

    fun modify(
        item: ItemStack,
        params: List<String>,
        holders: MutableMap<String, Any>
    ): Boolean {

        val variabled = params.map { it.replace(holders) }
        val type = variabled[0]
        val after = variabled.subList(1)

        when (type) {
            "" -> {}
            else -> return false
        }
        return true
    }
}