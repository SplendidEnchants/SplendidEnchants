package world.icebear03.splendidenchants.enchant.data.limitation

import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.util.replaceWithOrder
import taboolib.module.kether.compileToJexl
import world.icebear03.splendidenchants.api.EnchantAPI
import world.icebear03.splendidenchants.api.ItemAPI
import world.icebear03.splendidenchants.enchant.EnchantGroup
import world.icebear03.splendidenchants.enchant.SplendidEnchant
import world.icebear03.splendidenchants.enchant.data.limitation.LimitType.*

class Limitations(enchant: SplendidEnchant, lines: List<String>) {

    var belonging: SplendidEnchant = enchant

    var limitations = arrayListOf<Pair<LimitType, String>>()

    init {
        lines.forEach { limitations.add(LimitType.valueOf(it.split(":")[0]) to it.split(":")[1]) }
        limitations.add(MAX_CAPABILITY to "")
        limitations.add(TARGET to "")
    }

    // 检查操作是否被允许（比如是否可以附魔到某个物品上、使用时是否可以生效、村民生成新交易等）
    // item 就是跟操作直接有关的物品（如正在被附魔的书、正在使用的剑、生成的新交易中卖出的附魔书等）
    fun checkAvailable(checkType: CheckType, creature: LivingEntity?, item: ItemStack): Pair<Boolean, String> {
        for (limitation in limitations) {
            if (!checkType.containsType(limitation.first))
                continue

            val limitType = limitation.first
            val value = limitation.second
            when (limitType) {
                PAPI_EXPRESSION -> {
                    val player = if (creature is Player) creature else null
                    val currentExpression = value.replaceWithOrder(
                        *belonging.variable.generateReplaceMap(
                            ItemAPI.getLevel(item, belonging), player, item
                        ).toArray()
                    )
                    val result = value.compileToJexl().eval() as Boolean

                    //TODO 返回信息记得 replace 中文，比如 %player_level%>=30 应当翻译为 "经验等级>=30"
                    //TODO 等待语言文件写完
                    return result to currentExpression //TODO 翻译
                }

                PERMISSION -> {
                    if (creature == null)
                        return true to ""
                    if (!creature.hasPermission(value))
                        return false to "{limit.permission} || permission=$value"
                }

                CONFLICT_ENCHANT, DEPENDENCE_ENCHANT, CONFLICT_GROUP, DEPENDENCE_GROUP -> {
                    val result: Pair<Boolean, String> = checkAvailable(item)
                    if (!result.first)
                        return result
                }

                MAX_CAPABILITY -> {
                    val capability = Target.maxCapability(item.type)
                    if (ItemAPI.getEnchants(item).size >= capability) {
                        return false to "{limit.max_capability} || capability=$capability"
                    }
                }

                TARGET -> {
                    var flag = false
                    belonging.targets.forEach {
                        if (Target.isIn(it, item.type)) {
                            flag = true
                        }
                    }
                    if (!flag)
                        return false to "{limit.target} || capability=${belonging.targets}"
                }
            }
        }
        return true to ""
    }

    fun checkAvailable(item: ItemStack): Pair<Boolean, String> {
        for (limitation in limitations) {
            val limitType = limitation.first
            val value = limitation.second
            val enchant = EnchantAPI.getSplendidEnchant(value) ?: continue
            when (limitType) {
                CONFLICT_ENCHANT -> {
                    if (ItemAPI.containsEnchant(item, enchant))
                        return false to "{limit.conflict} || enchant=$value"
                }

                DEPENDENCE_ENCHANT -> {
                    if (!ItemAPI.containsEnchant(item, enchant))
                        return false to "{limit.conflict} || enchant=$value"
                }

                CONFLICT_GROUP -> { //特殊规则：多个共存机制 TODO 待完成
//                    ItemAPI.getEnchants(item).keys.forEach { it ->
//                        var amount = 0
//                        if (!EnchantAPI.isSame(it, belonging) && EnchantGroup.isIn(it, value))
//                            amount++
//                        if (amount > EnchantGroup.maxCoexist(value))
//                            return false to "{limit.conflict} || enchant=$value"
//                    }
                }

                DEPENDENCE_GROUP -> {
                    var flag = false
                    ItemAPI.getEnchants(item).keys.forEach { enchant ->
                        if (!EnchantAPI.isSame(enchant, belonging) && EnchantGroup.isIn(enchant, value))
                            flag = true
                    }
                    if (!flag)
                        return false to "{limit.dependence} || enchant=$value"
                }

                PAPI_EXPRESSION, PERMISSION -> {}
            }
        }
        return true to ""
    }

    fun conflictWith(enchant: Enchantment): Boolean {
        for (limitation in limitations) {
            val limitType = limitation.first
            val value = limitation.second
            if (limitType == CONFLICT_GROUP) {
                if (value == EnchantAPI.getName(enchant))
                    return true
            }
            if (limitType == CONFLICT_ENCHANT) {
                if (EnchantGroup.isIn(enchant, value))
                    return true
            }
        }
        return false
    }

    fun addLimitation(limitType: LimitType, value: String) {
        limitations.add(limitType to value)
    }
}
