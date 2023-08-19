package world.icebear03.splendidenchants.enchant.mechanism.entry.`object`

import org.bukkit.inventory.ItemStack
import world.icebear03.splendidenchants.api.replace

object ObjectItem {

    fun modify(
        item: ItemStack,
        params: List<String>,
        holders: MutableMap<String, Any>
    ): Boolean {

        val variabled = params.map { it.replace(holders) }

        when (variabled[0]) {
            "" -> {}
            else -> return false
        }
        return true
    }
}