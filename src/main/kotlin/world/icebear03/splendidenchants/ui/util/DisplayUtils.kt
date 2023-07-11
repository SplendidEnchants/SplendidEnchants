@file:Suppress("deprecation")
package world.icebear03.splendidenchants.ui.util

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import taboolib.common.util.replaceWithOrder
import taboolib.platform.util.modifyMeta
import world.icebear03.splendidenchants.enchant.SplendidEnchant

/**
 * SplendidEnchants
 * world.icebear03.splendidenchants.ui.util.DisplayUtils
 *
 * @author Mical
 * @since 2023/7/11 22:39
 */
infix fun Pair<ItemStack, Int>.applyReplaceMap(data: Pair<SplendidEnchant, Player>): ItemStack {
    val replaceMap = data.first.displayer.getReplaceMap(second, data.second, first)
    return first.modifyMeta<ItemMeta> {
        replaceMap.forEach {
            setDisplayName(displayName.replaceWithOrder(it))
            lore = lore?.map { line -> line.replaceWithOrder(it) }
        }
    }
}