package world.icebear03.splendidenchants.enchant

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.util.replaceWithOrder
import taboolib.platform.util.modifyLore
import world.icebear03.splendidenchants.Config
import world.icebear03.splendidenchants.api.ItemAPI
import world.icebear03.splendidenchants.enchant.data.Rarity
import kotlin.math.min

object EnchantDisplayer {

    var defaultPrevious: String
    var defaultSubsequent: String

    var sortByLevel: Boolean
    var rarityOrder: List<String>

    var combine: Boolean
    var minimal: Int
    var amount: Int
    var layouts: List<String>

    init {
        val config = Config.config.getConfigurationSection("display")!!
        defaultPrevious = config.getString("format.default_previous", "{color}{name} {roman_level}")!!
        defaultSubsequent = config.getString("format.default_subsequent", "\n§8| §7{description}")!!
        sortByLevel = config.getBoolean("sort.level", true)
        rarityOrder = config.getStringList("sort.rarity.order")
        Rarity.rarities.keys.forEach {
            if (!rarityOrder.contains(it))
                rarityOrder += it
        }

        combine = config.getBoolean("combine.enable", false)
        minimal = config.getInt("combine.min", 8)
        amount = config.getInt("combine.amount", 2)
        layouts = config.getStringList("combine.layout")
    }

    //对附魔排序
    fun sortEnchants(enchants: Map<SplendidEnchant, Int>): LinkedHashMap<SplendidEnchant, Int> {
        return LinkedHashMap(enchants.toSortedMap(Comparator.comparing {
            return@comparing rarityOrder.indexOf(it.rarity.id) * 100000 +
                    (if (sortByLevel) enchants[it]!! else 0)
        }))
    }

    //插入附魔对应的lore
    fun adaptItem(item: ItemStack?, player: Player?): ItemStack? {
        if (item == null) return item

        val enchants = sortEnchants(ItemAPI.getEnchants(item))
        val enchantLore = mutableListOf<String>()
        val combineMode = combine && enchants.size >= minimal

        if (!combineMode) {
            //未开启合并模式
            enchants.forEach {
                enchantLore += it.key.displayer.getSpecificDisplay(it.value, player, item).split("\n")
            }
        } else {
            //开启合并模式
            val enchantPairs = enchants.toList()
            for (i in 0 until enchants.size step amount) {
                //这一组有几个，并选取对应的layout布局模式
                val total = min(amount, enchants.size - i)
                var layout = layouts[total - 1]
                //替换变量
                for (j in 0 until total) {
                    val enchantPair = enchantPairs[i + j]
                    layout = layout.replaceWithOrder(
                        *enchantPair.first.displayer.getSpecificDisplayMap(
                            enchantPair.second,
                            player,
                            item,
                            j + 1
                        )
                    )
                }
                enchantLore += layout.split("\n")
            }
        }

        val origin = item.itemMeta.lore ?: emptyList()
        println(enchantLore)

        return item.modifyLore {
            clear()
            enchantLore + "§7" + origin
        }
    }

    //TODO 修改物品模块，注意PDC

    //TODO 回退物品模块，注意PDC，防止NBT堆叠造成卡顿
    //TODO 应注意创造模式的转换
}