package world.icebear03.splendidenchants.supports

import me.arasple.mc.trchat.module.internal.hook.type.HookDisplayItem
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import world.icebear03.splendidenchants.enchant.EnchantDisplayer

object HookTrChat : HookDisplayItem() {

    override fun displayItem(item: ItemStack, player: Player): ItemStack {
        return if (isHooked) EnchantDisplayer.display(item, player)
        else item
    }
}