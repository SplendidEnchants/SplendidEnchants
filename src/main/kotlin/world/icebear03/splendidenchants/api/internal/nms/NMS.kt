package world.icebear03.splendidenchants.api.internal.nms

import com.mcstarrysky.starrysky.function.emptyItemStack
import com.mojang.brigadier.StringReader
import net.md_5.bungee.api.chat.hover.content.Item
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.library.reflex.Reflex.Companion.invokeConstructor
import taboolib.library.reflex.Reflex.Companion.invokeMethod
import taboolib.module.nms.MinecraftVersion
import taboolib.module.nms.NMSItem
import taboolib.module.nms.nmsClass
import world.icebear03.splendidenchants.api.isNull
import world.icebear03.splendidenchants.enchant.EnchantDisplayer

abstract class NMS {

    /** 为原版的 MerchantRecipeList 的物品显示更多附魔 */
    abstract fun adaptMerchantRecipe(merchantRecipeList: Any, player: Player): Any

    /** 获取 BungeeCord 物品 Json */
    abstract fun itemToJson(item: Item): String

    /** 获取 Bukkit 物品 Json */
    abstract fun bkItemToJson(item: ItemStack): String

    /** 从 Json 获取 Bukkit 物品 */
    abstract fun jsonToItem(json: String): ItemStack
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

    override fun itemToJson(item: Item): String {
        return runCatching {
            nmsClass("MojangsonParser").invokeConstructor(StringReader(item.tag.nbt))
                .invokeMethod<Any>(if (MinecraftVersion.majorLegacy >= 11800) "readSingleStruct" else "a")?.toString() ?: "{}"
        }.getOrElse {
            println("itemToJson failed: $it")
            "{}"
        }
    }

    override fun bkItemToJson(item: ItemStack): String {
        return runCatching {
            NMSItem.asNMSCopy(item).invokeMethod<Any>("save", nmsClass("NBTTagCompound").newInstance()).toString()
        }.getOrElse {
            println("bkItemToJson failed: $it")
            "{}"
        }
    }

    override fun jsonToItem(json: String): ItemStack {
        return runCatching {
            val nbt = nmsClass("MojangsonParser").invokeMethod<Any>(if (MinecraftVersion.majorLegacy >= 11800) "parseTag" else "parse", json, isStatic = true)
            println("parse出来的 java class: " + nbt?.javaClass?.name)
            NMSItem.asBukkitCopy(nmsClass("ItemStack").invokeConstructor(nbt))
        }.getOrElse {
            println("jsonToItem failed: $it")
            emptyItemStack
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