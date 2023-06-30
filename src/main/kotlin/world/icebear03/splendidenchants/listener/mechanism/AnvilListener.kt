package world.icebear03.splendidenchants.listener.mechanism

import org.bukkit.entity.Player
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.kether.compileToJexl
import world.icebear03.splendidenchants.api.ItemAPI
import world.icebear03.splendidenchants.enchant.data.limitation.CheckType
import world.icebear03.splendidenchants.util.YamlUpdater
import kotlin.math.floor

object AnvilListener {

    val allowUnsafeLevel: Boolean
    val allowUnsafeCombine: Boolean

    val maxCost: Int
    val renameCost: Int
    val repairCost: Int
    val newEnchantExtraCost: Int
    val enchantCostPerLevel: String

    val allowDiffenrentMaterial: Boolean
    val privilege = mapOf("default" to "{cost_level}*1.0").toMutableMap()

    init {
        val config = YamlUpdater.loadAndUpdate("mechanisms/anvil.yml")
        allowUnsafeLevel = config.getBoolean("limit.unsafe_level", true)
        allowUnsafeCombine = config.getBoolean("limit.unsafe_combine", false)

        maxCost = config.getInt("max_cost", 100)
        renameCost = config.getInt("rename_cost", 3)
        repairCost = config.getInt("repair_cost", 5)
        newEnchantExtraCost = config.getInt("enchant_cost.new_extra", 2)
        enchantCostPerLevel = config.getString("enchant_cost.per_level", "6.0/{max_level}")!!

        allowDiffenrentMaterial = config.getBoolean("allow_different_material", false)
        config.getStringList("privilege").forEach { it ->
            privilege[it.split(":")[0]] = it.split(":")[1]
        }
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
        if (second == null) {
            if (renameText == null)
                return
            if (renameText != ItemAPI.getName(first)) {
                inv.repairCost = renameCost
                //如果交给原版处理rename，则会丢附魔
                inv.result = ItemAPI.setName(first.clone(), renameText)
            }
            return
        }

        //修理
        if (ItemAPI.getEnchants(second).isEmpty()) {
            if (result == null)
                return
            inv.repairCost = repairCost
            //如果交给原版处理durability，则会丢附魔
            //所以我们先获取新的durability，然后再重新造一个物品
            inv.result = ItemAPI.setDamage(first.clone(), ItemAPI.getDamage(result))
            return
        }

        //拼合附魔（重点！）
        val newResult = combine(first, second, player)
        if (newResult.second <= 0 || newResult.first == null) {
            inv.result = null
            return
        }
        inv.result = newResult.first

        //特权减免等级消耗处理
        var finalCost = privilege["default"]!!.compileToJexl().eval(mapOf("{cost_level}" to newResult.second)) as Double
        privilege.forEach {
            if (it.key != "default" && player.hasPermission(it.value))
                finalCost =
                    minOf(finalCost, it.value.compileToJexl().eval(mapOf("{cost_level}" to newResult.second)) as Double)
        }

        //不能低于1级
        inv.repairCost = maxOf(1, floor(finalCost).toInt())
    }

    //返回的是 拼合结果，耗费经验
    fun combine(first: ItemStack, second: ItemStack, player: Player): Pair<ItemStack?, Double> {
        if (first.type != second.type) {
            return null to 0.0
        }

        val result = first.clone()
        var costLevel = 0.0
        for (it in ItemAPI.getEnchants(second)) {
            if (it.key.limitations.checkAvailable(CheckType.ANVIL, player, first).first) {
                var originLevel = ItemAPI.getLevel(result, it.key)
                val perLevel = enchantCostPerLevel.compileToJexl().eval(mapOf("max_level" to it.key.maxLevel)) as Double
                if (originLevel <= 0) {
                    originLevel = 0
                    costLevel += newEnchantExtraCost
                }
                if (originLevel < it.value) {
                    if (it.value >= it.key.maxLevel && !allowUnsafeLevel) {
                        continue
                    }
                    ItemAPI.addEnchant(result, it.key, it.value)
                    costLevel += perLevel * (it.value - originLevel)
                }
                if (originLevel == it.value) {
                    if (it.value >= it.key.maxLevel && !allowUnsafeCombine) {
                        continue
                    }
                    ItemAPI.addEnchant(result, it.key, it.value + 1)
                    costLevel += perLevel
                }
            }
        }
        return result to costLevel
    }
}