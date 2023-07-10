package world.icebear03.splendidenchants.api.nms

import world.icebear03.splendidenchants.`object`.Overlay
import org.bukkit.boss.BarColor
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.io.getClass
import taboolib.common.platform.function.submit
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.library.reflex.Reflex.Companion.setProperty
import taboolib.library.reflex.Reflex.Companion.unsafeInstance
import taboolib.module.chat.colored
import taboolib.module.nms.MinecraftVersion
import taboolib.platform.util.isAir
import world.icebear03.splendidenchants.enchant.EnchantDisplayer
import java.util.UUID

/**
 * SplendidEnchants
 * world.icebear03.splendidenchants.api.nms.NMSImpl
 *
 * @author mical
 * @since 2023/6/21 10:28 PM
 */
class NMSImpl : NMS() {

    override fun sendBossBar(player: Player, message: String, progress: Float, time: Int, overlay: Overlay, color: BarColor) {
        val uuid = UUID.randomUUID()
        when (MinecraftVersion.major) {
            // 1.16
            8 -> {
                sendPacket(
                    player,
                    NMS16PacketPlayOutBoss(),
                    "a" to uuid,
                    "b" to NMS16PacketPlayOutBossAction.ADD,
                    "c" to CraftChatMessage16.fromString(message.colored()).first(),
                    "d" to progress,
                    "e" to NMS16BossBattleBarColor.valueOf(color.name.uppercase()),
                    "f" to NMS16BossBattleBarStyle.valueOf(overlay.name.uppercase()),
                    "g" to false,
                    "h" to false,
                    "i" to false
                )
                submit(delay = time * 20L) {
                    sendPacket(
                        player,
                        NMS16PacketPlayOutBoss(),
                        "a" to uuid,
                        "b" to NMS16PacketPlayOutBossAction.REMOVE
                    )
                }
            }
            // 1.17, 1.18, 1.19, 1.20
            in 9..12 -> {
                sendPacket(
                    player,
                    NMSPacketPlayOutBoss::class.java.unsafeInstance(),
                    "id" to uuid,
                    "operation" to getClass("net.minecraft.network.protocol.game.PacketPlayOutBoss\$a").unsafeInstance().also {
                        it.setProperty("name", CraftChatMessage19.fromString(message.colored()).first())
                        it.setProperty("progress", progress)
                        it.setProperty("color", NMSBossBattleBarColor.valueOf(color.name.uppercase()))
                        it.setProperty("overlay", NMSBossBattleBarStyle.valueOf(overlay.name.uppercase()))
                        it.setProperty("darkenScreen", false)
                        it.setProperty("playMusic", false)
                        it.setProperty("createWorldFog", false)
                    }
                )
                submit(delay = time * 20L) {
                    sendPacket(
                        player,
                        NMSPacketPlayOutBoss::class.java.unsafeInstance(),
                        "id" to uuid,
                        "operation" to NMSPacketPlayOutBoss::class.java.getProperty<Any>("REMOVE_OPERATION", true)!!
                    )
                }
            }
            // Unsupported
            else -> error("Unsupported version.")
        }
    }

    override fun toBukkitItemStack(item: Any): ItemStack {
        return when (MinecraftVersion.major) {
            // 1.16
            8 -> CraftItemStack16.asBukkitCopy(item as NMS16ItemStack)
            // 1.17, 1.18, 1.19, 1.20
            in 9..12 -> CraftItemStack19.asBukkitCopy(item as NMSItemStack)
            // Unsupported
            else -> error("Unsupported version.")
        }
    }

    /**
     * 需要强转
     * 1.16 -> NMS16ItemStack
     * 1.17, 1.18, 1.19, 1.20 -> NMSItemStack
     */
    override fun toNMSItemStack(item: ItemStack): Any {
        return when (MinecraftVersion.major) {
            // 1.16
            8 -> CraftItemStack16.asNMSCopy(item)
            // 1.17, 1.18, 1.19, 1.20
            in 9..12 -> CraftItemStack19.asNMSCopy(item)
            // Unsupported
            else -> error("Unsupported version.")
        }
    }

    override fun adaptMerchantRecipe(merchantRecipeList: Any, player: Player): Any {

        fun adapt(item: Any, player: Player): Any {
            val bkItem = toBukkitItemStack(item)
            if (bkItem.isAir) return item
            return toNMSItemStack(EnchantDisplayer.display(bkItem, player))
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