package world.icebear03.splendidenchants.listener.mechanism

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import taboolib.module.kether.compileToJexl
import world.icebear03.splendidenchants.api.ItemAPI
import world.icebear03.splendidenchants.enchant.data.limitation.CheckType
import world.icebear03.splendidenchants.util.YamlUpdater

object AnvilListener {

    var allowUnsafeLevel = true
    var allowUnsafeCombine = false

    var maxCost = 100
    var renameCost = 3
    var repairCost = 5
    var newEnchantExtraCost = 2
    var enchantCostPerLevel = "6.0/{max_level}"

    var allowDiffenrentMaterial = false
    var privilege = mutableMapOf<String, String>()

    fun initialize() {
        val config = YamlUpdater.loadAndUpdate("mechanisms/anvil.yml")
        allowUnsafeLevel = config.getBoolean("limit.unsafe_level", true)
        allowUnsafeCombine = config.getBoolean("limit.unsafe_combine", false)

        maxCost = config.getInt("max_cost", 100)
        renameCost = config.getInt("rename_cost", 3)
        repairCost = config.getInt("repair_cost", 5)
        newEnchantExtraCost = config.getInt("enchant_cost.new_extra", 2)
        enchantCostPerLevel = config.getString("enchant_cost.per_level", enchantCostPerLevel)!!

        allowDiffenrentMaterial = config.getBoolean("allow_different_material", false)
        privilege.clear()
        config.getStringList("privilege").forEach { it ->
            privilege[it.split(":")[0]] = it.split(":")[1]
        }

        info("    Successfully load anvil module")
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun event(event: PrepareAnvilEvent) {
        val inv = event.inventory
        val first = inv.firstItem
        val second = inv.secondItem
        val result = inv.result
        val renameText = inv.renameText
        val player = event.viewers[0] as Player

        if (first == null)
            return

        //重命名
        //如果交给原版处理rename，则会丢附魔
        if (second == null) {
            if (renameText != ItemAPI.getName(first)) {
                inv.repairCost = finalCost(renameCost + 0.0, player)
                event.result = ItemAPI.setName(first.clone(), renameText)
            }
            return
        }

        //修理
        //如果交给原版处理durability，则会丢附魔
        if (ItemAPI.getEnchants(second).isEmpty()) {
            var cost = repairCost
            val item = ItemAPI.setDamage(first.clone(), ItemAPI.getDamage(result))

            if (renameText != ItemAPI.getName(first)) {
                cost += renameCost
                event.result = ItemAPI.setName(item, renameText)
            } else {
                event.result = item
            }
            inv.repairCost = finalCost(cost + 0.0, player)
            return
        }

        //拼合附魔（重点！）
        val resultAndCost = combine(first, second, player)
        var cost = resultAndCost.second + 0.0
        if (cost <= 0 || resultAndCost.first == null) {
            event.result = null
            return
        }
        if (resultAndCost.first!!.isSimilar(first)) {
            event.result = null
            return
        }

        if (renameText != ItemAPI.getName(first)) {
            cost += renameCost
            event.result = ItemAPI.setName(resultAndCost.first!!, renameText)
        } else {
            event.result = resultAndCost.first
        }

        inv.repairCost = finalCost(cost, player)
    }

    //返回的是 新物品，耗费经验
    fun combine(first: ItemStack, second: ItemStack, player: Player): Pair<ItemStack?, Double> {
        if (first.type != second.type && second.type != Material.ENCHANTED_BOOK) {
            return null to 0.0
        }

        val result = first.clone()
        var costLevel = 0.0
        for (it in ItemAPI.getEnchants(second)) {
            val enchant = it.key
            val level = it.value
            val maxLevel = it.key.maxLevel
            var originLevel = ItemAPI.getLevel(result, enchant)
            var newLevel = originLevel

            if (enchant.limitations.checkAvailable(CheckType.ANVIL, player, first).first) {
                val perLevel = enchantCostPerLevel.replace("{max_level}", it.key.maxLevel.toString()).compileToJexl()
                    .eval() as Double
                //是新的附魔
                if (originLevel <= 0) {
                    originLevel = 0
                    costLevel += newEnchantExtraCost
                }
                if (originLevel < level) {
                    newLevel = level
                    //不允许超出最高级
                    if (level >= maxLevel) {
                        if (!allowUnsafeLevel) {
                            newLevel = maxLevel
                        }
                    }
                }
                if (originLevel == level) {
                    if (level >= maxLevel && !allowUnsafeCombine) {
                        continue
                    }
                    newLevel = level + 1
                }
                ItemAPI.addEnchant(result, enchant, newLevel)
                costLevel += perLevel * (newLevel - originLevel)
            }
        }
        return result to costLevel
    }

    fun finalCost(origin: Double, player: Player): Int {
        var minCost = origin
        privilege.forEach {
            if (player.hasPermission(it.key)) {
                val newCost = it.value.replace("{cost_level}", origin.toString()).compileToJexl().eval() as Double
                minCost = minOf(minCost, newCost)
            }
        }

        //不能低于1级，不能高于设定好的上限等级
        return minOf(maxCost, maxOf(minCost, 1.0).toInt())
    }
}