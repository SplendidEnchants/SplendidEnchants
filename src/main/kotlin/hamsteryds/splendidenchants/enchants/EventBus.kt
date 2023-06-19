package hamsteryds.splendidenchants.enchants

import org.bukkit.Material
import org.bukkit.event.block.BlockBreakEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.util.sendActionBar

/**
 * SplendidEnchants
 * hamsteryds.splendidenchants.enchants.EventBus
 *
 * @author mical
 * @since 2023/6/19 12:35 PM
 */
object EventBus {

    @SubscribeEvent
    fun e(e: BlockBreakEvent) {
        e.player.sendActionBar("测试")
        val item = e.player.equipment!!.itemInMainHand
        if (item.type == Material.IRON_AXE && item.containsEnchantment(EnchantmentLoader.enchants["nmsl:nmsl"]!!)) {
            e.player.sendMessage("你妈死了你妈死了你妈死了")
            e.player.sendActionBar("你妈死了你妈死了你妈死了") // NMS Test
        }
    }
}