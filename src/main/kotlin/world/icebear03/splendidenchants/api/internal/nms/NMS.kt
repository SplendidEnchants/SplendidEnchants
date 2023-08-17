package world.icebear03.splendidenchants.api.internal.nms

import org.bukkit.entity.Player
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.NMSItem
import world.icebear03.splendidenchants.api.isNull
import world.icebear03.splendidenchants.enchant.EnchantDisplayer

abstract class NMS {

    /** 为原版的 MerchantRecipeList 的物品显示更多附魔 **/
    abstract fun adaptMerchantRecipe(merchantRecipeList: Any, player: Player): Any
}

class NMSImpl : NMS() {

    override fun adaptMerchantRecipe(merchantRecipeList: Any, player: Player): Any {

        fun adapt(item: Any, player: Player): Any {
            val bkItem = NMSItem.asBukkitCopy(item)
            if (bkItem.isNull) return item
            return NMSItem.asNMSCopy(EnchantDisplayer.display(bkItem, player))
        }

        return when (MinecraftVersion.major) {
            // 1.16
            8 -> {
                val previous = merchantRecipeList as NMS16MerchantRecipeList
                val adapt = NMS16MerchantRecipeList()
                for (i in 0 until previous.size) {
                    val recipe = previous[i]!!
                    adapt += NMS16MerchantRecipe(
                        adapt(recipe.buyingItem1, player) as NMS16ItemStack,
                        adapt(recipe.buyingItem2, player) as NMS16ItemStack,
                        adapt(recipe.sellingItem, player) as NMS16ItemStack,
                        recipe.uses,
                        recipe.maxUses,
                        recipe.xp,
                        recipe.priceMultiplier,
                        recipe.demand
                    )
                }
                adapt
            }
            // 1.17, 1.18, 1.19, 1.20
            in 9..12 -> {
                val previous = merchantRecipeList as NMSMerchantRecipeList
                val adapt = NMSMerchantRecipeList()
                for (i in 0 until previous.size) {
                    val recipe = previous[i]!!
                    adapt += NMSMerchantRecipe(
                        adapt(recipe.baseCostA, player) as NMSItemStack,
                        adapt(recipe.costB, player) as NMSItemStack,
                        adapt(recipe.result, player) as NMSItemStack,
                        recipe.uses,
                        recipe.maxUses,
                        recipe.xp,
                        recipe.priceMultiplier,
                        recipe.demand
                    )
                }
                adapt
            }
            // Unsupported
            else -> error("Unsupported version.")
        }
    }
}

// 1.16
typealias NMS16ItemStack = net.minecraft.server.v1_16_R3.ItemStack

typealias NMS16MerchantRecipe = net.minecraft.server.v1_16_R3.MerchantRecipe

typealias NMS16MerchantRecipeList = net.minecraft.server.v1_16_R3.MerchantRecipeList

// Universal
typealias NMSItemStack = net.minecraft.world.item.ItemStack

typealias NMSMerchantRecipe = net.minecraft.world.item.trading.MerchantRecipe

typealias NMSMerchantRecipeList = net.minecraft.world.item.trading.MerchantRecipeList