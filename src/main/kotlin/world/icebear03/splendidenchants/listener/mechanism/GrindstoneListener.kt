package world.icebear03.splendidenchants.listener.mechanism

import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.util.replaceWithOrder
import taboolib.module.kether.compileToJexl
import world.icebear03.splendidenchants.api.ItemAPI
import world.icebear03.splendidenchants.enchant.EnchantGroup
import world.icebear03.splendidenchants.util.YamlUpdater
import kotlin.math.roundToInt


object GrindstoneListener {

    val enableVanilla: Boolean
    val enableCustomGrindstone: Boolean

    val expPerEnchant: String
    val accumulation: Boolean
    val rarityBonus = mutableMapOf<String, Double>()
    val defaultBonus: Double
    val blacklist: String

    val privilege = mutableMapOf<String, String>()

    init {
        val config = YamlUpdater.loadAndUpdate("mechanisms/grindstone.yml")
        enableVanilla = config.getBoolean("grindstone.vanilla", false)
        enableCustomGrindstone = config.getBoolean("grindstone.custom", true)

        expPerEnchant = config.getString("exp_per_enchant", "30*{level}/{max_level}*{rarity_bonus}")!!
        accumulation = config.getBoolean("accumulation", true)
        val section = config.getConfigurationSection("rarity_bonus")!!
        section.getKeys(false).forEach {
            rarityBonus[it] = section.getDouble(it)
        }
        defaultBonus = config.getDouble("default_bonus", 1.0)
        blacklist = config.getString("blacklist_group", "不可磨砂类附魔")!!

        config.getStringList("privilege").forEach { it ->
            privilege[it.split(":")[0]] = it.split(":")[1]
        }
    }


    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun event(event: InventoryOpenEvent) {
        val inv = event.inventory
        if (inv.type == InventoryType.GRINDSTONE) {
            if (!enableVanilla) {
                val player = event.player
                if (enableCustomGrindstone) {
                    //TODO 打开砂轮界面
                } else {
                    player.sendMessage("本服务器未启用原版砂轮界面！")
                    event.isCancelled = true
                }
            }
        }
    }

    //返回内容：磨砂后的物品，返还的经验点数
    //如果磨砂前后不变，按理来说经验应该为0
    fun grind(player: Player, item: ItemStack): Pair<ItemStack, Int> {
        var cost = 0.0
        val grinded = ItemAPI.clearEnchants(item.clone())
        ItemAPI.getEnchants(item).forEach {
            val enchant = it.key
            val level = it.value
            val maxLevel = enchant.maxLevel
            if (EnchantGroup.isIn(enchant, blacklist)) {
                ItemAPI.addEnchant(grinded, enchant, level)
            } else {
                var bonus = defaultBonus
                if (rarityBonus.containsKey(enchant.rarity.id))
                    bonus = rarityBonus[enchant.rarity.id]!!
                if (rarityBonus.containsKey(enchant.rarity.name))
                    bonus = rarityBonus[enchant.rarity.name]!!
                val refund = expPerEnchant.replaceWithOrder(
                    level to "level",
                    maxLevel to "maxLevel",
                    rarityBonus to "rarity_bonus"
                ).compileToJexl().eval() as Double

                if (accumulation)
                    cost += refund
                else
                    cost = maxOf(cost, refund)
            }
        }

        return grinded to finalCost(cost, player)
    }

    fun finalCost(origin: Double, player: Player): Int {
        var maxRefund = origin
        AnvilListener.privilege.forEach {
            if (player.hasPermission(it.key)) {
                val newRefund = it.value.replace("{refund_exp}", origin.toString()).compileToJexl().eval() as Double
                maxRefund = maxOf(maxRefund, newRefund)
            }
        }

        return maxOf(0, maxRefund.roundToInt())
    }
}