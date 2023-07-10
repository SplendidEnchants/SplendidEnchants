package world.icebear03.splendidenchants.listener.packet

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.trading.MerchantRecipe
import net.minecraft.world.item.trading.MerchantRecipeList
import org.bukkit.entity.Player
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.PacketSendEvent
import taboolib.platform.util.isAir
import world.icebear03.splendidenchants.api.nms.NMS
import world.icebear03.splendidenchants.enchant.EnchantDisplayer

object PacketOpenWindowMerchant {

    @SubscribeEvent(priority = EventPriority.MONITOR)
    fun e(e: PacketSendEvent) {
        if (e.packet.name == "PacketPlayOutOpenWindowMerchant") {
            when (MinecraftVersion.major) {
                // 1.16 -> b
                8 -> {
                    val merchant = e.packet.read<MerchantRecipeList>("b")!!
                    val adaptedMerchant = MerchantRecipeList()
                    for (i in 0..merchant.size) {
                        val recipe = merchant[i]!!
                        val adaptedRecipe = MerchantRecipe(
                            adapt(recipe.baseCostA, e.player),
                            adapt(recipe.costB, e.player),
                            adapt(recipe.result, e.player),
                            recipe.uses,
                            recipe.maxUses,
                            recipe.xp,
                            recipe.priceMultiplier,
                            recipe.demand
                        )

                        adaptedMerchant += recipe
                    }

                    e.packet.write("b", adaptedMerchant)
                }
                // 1.17, 1.18, 1.19, 1.20 -> b
                //需要改一下关于recipe field的调用(a,b,c,d,e,f,g)
                9, 10, 11, 12 -> {
                    val merchant = e.packet.read<MerchantRecipeList>("b")!!
                    val adaptedMerchant = MerchantRecipeList()
                    for (i in 0..merchant.size) {
                        val recipe = merchant[i]!!
                        println(recipe.javaClass.fields.toList())
                        val adaptedRecipe = MerchantRecipe(
                            adapt(recipe.baseCostA, e.player), //TODO baseCostA?
                            adapt(recipe.costB, e.player),
                            adapt(recipe.result, e.player),
                            recipe.uses,
                            recipe.maxUses,
                            recipe.xp,
                            recipe.priceMultiplier,
                            recipe.demand
                        )

                        adaptedMerchant += recipe
                    }

                    e.packet.write("b", adaptedMerchant)
                }

                else -> error("Unsupported version.")
            }
        }
    }

    private fun adapt(item: ItemStack, player: Player): ItemStack {
        val bkItem = NMS.INSTANCE.toBukkitItemStack(item)
        if (bkItem.isAir) return item
        return NMS.INSTANCE.toNMSItemStack(EnchantDisplayer.display(bkItem, player)) as ItemStack
    }
}