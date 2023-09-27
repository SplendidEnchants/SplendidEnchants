package world.icebear03.splendidenchants.listener.mechanism

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.console
import taboolib.common5.cdouble
import world.icebear03.splendidenchants.api.*
import world.icebear03.splendidenchants.api.internal.YamlUpdater
import world.icebear03.splendidenchants.enchant.data.belongedTargets
import world.icebear03.splendidenchants.enchant.data.limitation.CheckType
import kotlin.math.ceil
import kotlin.math.roundToInt

object AnvilListener {

    var allowUnsafeLevel = true
    var allowUnsafeCombine = false

    var maxCost = 100
    var renameCost = 3
    var repairCost = 5
    var newEnchantExtraCost = 2
    var enchantCostPerLevel = "6.0/{max_level}"

    var allowDifferentMaterial = false
    var privilege = mutableMapOf<String, String>()

    fun load() {
        YamlUpdater.loadAndUpdate("mechanisms/anvil.yml").run {
            allowUnsafeLevel = getBoolean("limit.unsafe_level", true)
            allowUnsafeCombine = getBoolean("limit.unsafe_combine", false)

            maxCost = getInt("max_cost", 100)
            renameCost = getInt("rename_cost", 3)
            repairCost = getInt("repair_cost", 5)
            newEnchantExtraCost = getInt("enchant_cost.new_extra", 2)
            enchantCostPerLevel = getString("enchant_cost.per_level", enchantCostPerLevel)!!

            allowDifferentMaterial = getBoolean("allow_different_material", false)
            privilege.clear()
            privilege.putAll(getStringList("privilege").map { it.split(":")[0] to it.split(":")[1] })
        }

        console().sendMessage("    Successfully load anvil module")
    }

    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun anvil(event: PrepareAnvilEvent) {
        val inv = event.inventory
        val player = event.viewers[0] as Player

        val a = inv.firstItem ?: return
        val b = inv.secondItem

        var renameText = event.result?.name
        if (renameText?.isBlank() != false)
            renameText = inv.renameText

        val result = anvil(a, b, player, renameText)

        result.first ?: return
        event.result = result.first
        inv.repairCost = result.second
        inv.repairCostAmount = result.third
        inv.maximumRepairCost = 100
    }

    //第三个数据是消耗的修复物品数量
    fun anvil(a: ItemStack, b: ItemStack?, player: Player, name: String? = null): Triple<ItemStack?, Int, Int> {
        val typeA = a.type
        val typeB = b?.type ?: Material.AIR

        val result = a.clone()
        var cost = 0.0

        var amount = 1

        name?.let {
            if (it.isNotBlank()) {
                result.name = it
                cost += renameCost
            }
        }
        if (b != null)
            if (b.canRepair(a) || typeB == typeA) {
                val pair = durabilityFixed(typeA, typeB, b.amount, a.damage, b.damage)
                result.damage = maxOf(0, result.damage - pair.first)
                cost += repairCost
                amount = pair.second
            }

        if (typeA == typeB || typeB == Material.ENCHANTED_BOOK ||
            (allowDifferentMaterial && typeB.belongedTargets.any { !typeA.belongedTargets.contains(it) })
        ) {
            val tmp = a.clone()
            b!!.fixedEnchants.filterKeys {
                val checked = it.limitations.checkAvailable(CheckType.ANVIL, tmp, player)
                if (checked.first) tmp.addEt(it, b.etLevel(it))
                checked.first
            }.forEach { (enchant, lv) ->
                val old = a.etLevel(enchant)
                val new = if (old < lv) {
                    if (old <= 0) cost += newEnchantExtraCost
                    if (lv > enchant.maxLevel && !allowUnsafeLevel) return@forEach
                    lv
                } else if (old == lv) {
                    if (old >= enchant.maxLevel && !allowUnsafeCombine) return@forEach
                    lv + 1
                } else return@forEach
                result.addEt(enchant, new)
                cost += enchantCostPerLevel.calcToDouble("max_level" to enchant.maxLevel) * (new - old.coerceAtLeast(0))
            }
        }

        if (cost == 0.0 || result == a)
            return Triple(null, 0, 0)

        return Triple(result, finalCost(cost, player), amount)
    }

    fun durabilityFixed(type: Material, fixer: Material, amount: Int, dmgA: Int, dmgB: Int): Pair<Int, Int> {
        val typeS = type.toString()
        val fixerS = fixer.toString()
        val isArmor = typeS.contains("HELMET") ||
                typeS.contains("CHESTPLATE") ||
                typeS.contains("LEGGINGS") ||
                typeS.contains("BOOTS") ||
                type == Material.SHIELD
        var fixed = -1
        if (typeS.startsWith("WOODEN_") && fixerS.endsWith("PLANKS"))
            fixed = if (isArmor) 84 else 14
        if (typeS.startsWith("STONE_") && (fixer == Material.BLACKSTONE || fixer == Material.COBBLESTONE || fixer == Material.DEEPSLATE))
            fixed = 32
        if (typeS.startsWith("IRON_") && fixer == Material.IRON_INGOT)
            fixed = if (isArmor) 56 else 62
        if (typeS.startsWith("DIAMOND_") && fixer == Material.DIAMOND)
            fixed = if (isArmor) 123 else 390
        if (typeS.startsWith("NETHERITE_") && fixer == Material.NETHERITE_INGOT)
            fixed = if (isArmor) 138 else 507
        if (typeS.startsWith("GOLD_") && fixer == Material.GOLD_INGOT)
            fixed = if (isArmor) 26 else 8
        if (typeS.startsWith("LEATHER_") && fixer == Material.LEATHER)
            fixed = 18
        if (type == Material.ELYTRA && fixer == Material.PHANTOM_MEMBRANE)
            fixed = 108
        if (type == Material.TURTLE_HELMET && fixer == Material.SCUTE)
            fixed = 68
        if (type == fixer)
            fixed = fixer.maxDurability - dmgB

        val minAmount = minOf(ceil(dmgA.cdouble / fixed).roundToInt(), amount)
        return (fixed * minAmount).coerceAtMost(type.maxDurability.toInt()) to minAmount
    }

    fun finalCost(origin: Double, player: Player) = privilege.minOf { (perm, expression) ->
        if (player.hasPermission(perm)) expression.calcToInt("cost" to origin)
        else origin.roundToInt()
    }.coerceAtMost(maxCost).coerceAtLeast(1)
}