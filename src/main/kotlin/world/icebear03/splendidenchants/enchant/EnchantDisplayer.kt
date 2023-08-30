package world.icebear03.splendidenchants.enchant

import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import taboolib.common.platform.function.console
import taboolib.platform.util.modifyMeta
import world.icebear03.splendidenchants.api.*
import world.icebear03.splendidenchants.api.internal.YamlUpdater
import world.icebear03.splendidenchants.enchant.data.capability
import world.icebear03.splendidenchants.enchant.data.rarities
import kotlin.math.min

object EnchantDisplayer {

    var defaultPrevious = "{enchant_display_roman}"
    var defaultSubsequent = "\n§8| §7{description}"
    var capabilityLine = "§8| §7附魔词条数空余: §e{capability}"

    var sortByLevel = true
    var rarityOrder = listOf<String>()

    var combine = true
    var minimal = 8
    var amount = 2
    var layouts = listOf<String>()

    var loreFormation = mutableMapOf<Boolean, List<String>>()

    fun load() {
        YamlUpdater.loadAndUpdate("enchants/display.yml").run {
            defaultPrevious = getString("format.default_previous", defaultPrevious)!!
            defaultSubsequent = getString("format.default_subsequent", defaultSubsequent)!!
            capabilityLine = getString("capability_line", capabilityLine)!!

            sortByLevel = getBoolean("sort.level", true)
            rarityOrder = getStringList("sort.rarity.order") + rarities.keys.filter { !rarityOrder.contains(it) }

            combine = getBoolean("combine.enable", true)
            minimal = getInt("combine.min", 8)
            amount = getInt("combine.amount", 2)
            layouts = getStringList("combine.layout")

            loreFormation[true] = getStringList("lore_formation.has_lore")
            loreFormation[false] = getStringList("lore_formation.without_lore")
        }

        console().sendMessage("    Successfully load enchant displayer module")
    }

    //对附魔排序
    fun sortEnchants(enchants: Map<SplendidEnchant, Int>): LinkedHashMap<SplendidEnchant, Int> {
        return linkedMapOf(*enchants.toList().sortedBy { (enchant, level) ->
            rarityOrder.indexOf(enchant.rarity.id) * 100000 + (if (sortByLevel) level else 0)
        }.toTypedArray())
    }

    //插入附魔对应的lore
    fun generateLore(item: ItemStack? = null, player: Player? = null): List<String> {
        item ?: return listOf()

        val enchants = sortEnchants(item.fixedEnchants).ifEmpty { return listOf() }
        val lore = mutableListOf<String>()
        val combineMode = combine && enchants.size >= minimal

        if (!combineMode) lore += enchants.map { (enchant, level) -> enchant.displayer.display(level, player, item) }
        else {
            val enchantPairs = enchants.toList()
            for (i in 0 until enchants.size step amount) {
                val total = min(amount, enchants.size - i)
                var layout = layouts[total - 1]
                for (j in 0 until total) {
                    val enchantPair = enchantPairs[i + j]
                    layout = layout.replace(enchantPair.first.displayer.displays(enchantPair.second, player, item, j + 1))
                }
            }
        }

        return lore.flatMap { it.split("\n") }
    }

    //展示是给玩家看的，玩家必须存在
    fun display(item: ItemStack, player: Player): ItemStack {
        if (item.isNull) return item
        return item.clone().modifyMeta<ItemMeta> {
            item.fixedEnchants.ifEmpty { return@modifyMeta }

            //已经展示过了就重新展示（2.0遗留思路）（个人认为多余，可以尝试去除此处，节省性能）
            this["display_mark", PersistentDataType.BOOLEAN]?.let { return@modifyMeta }

            //若本来就不需要显示附魔，就不显示了
            //注意，附魔书对应的隐藏附魔flag是HIDE POTION EFFECTS而不是HIDE ENCHANTS（1.18-是这样，1.19+未知）
            if (item.isEnchantedBook)
                if (hasItemFlag(ItemFlag.HIDE_ENCHANTS) || hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS)) return@modifyMeta
                else addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS)
            else
                if (hasItemFlag(ItemFlag.HIDE_ENCHANTS)) return@modifyMeta
                else addItemFlags(ItemFlag.HIDE_ENCHANTS)

            //上标记
            this["display_mark", PersistentDataType.BOOLEAN] = true

            //上lore
            val enchantLore = generateLore(item, player).toMutableList()
            val originLore = lore ?: emptyList()
            val resultLore = mutableListOf<String>()
            var first = 0
            var last = 0
            loreFormation[originLore.isNotEmpty()]!!.forEach {
                when (it) {
                    "{enchant_lore}" -> resultLore += enchantLore
                    "{capability_line}" -> resultLore += capabilityLine.replace("capability" to item.type.capability - item.fixedEnchants.size)
                    "{item_lore}" -> {
                        first = resultLore.size
                        resultLore += originLore
                        last = resultLore.size
                    }

                    else -> resultLore += it
                }
            }
            lore = resultLore
            this["lore_index", PersistentDataType.STRING] = "$first-$last"
            //加附魔序列化数据
            this["enchants_serialized", PersistentDataType.STRING] =
                item.fixedEnchants.map { (enchant, level) -> "${enchant.basicData.id}:$level" }.joinToString("|")
        }
    }

    fun undisplay(item: ItemStack, player: Player): ItemStack {
        if (item.isNull) return item
        return item.clone().modifyMeta<ItemMeta> {
            this["display_mark", PersistentDataType.BOOLEAN] ?: return@modifyMeta

            removeItemFlags(ItemFlag.HIDE_ENCHANTS)
            if (item.isEnchantedBook) removeItemFlags(ItemFlag.HIDE_POTION_EFFECTS)

            //创造模式的额外处理，需要重新给物品附魔
            //这是因为创造模式下客户端会重新设置背包物品，而重新设置的物品中非原版附魔会消失
            //这是由于客户端没有注册更多附魔
            if (player.gameMode == GameMode.CREATIVE) {
                this["enchants_serialized", PersistentDataType.STRING]!!.split("|").forEach { pair ->
                    splendidEt(pair.split(":")[0])?.let { enchant ->
                        addEt(enchant, pair.split(":")[1].toInt())
                    }
                }
            }

            //除去enchant lore
            val index = this["lore_index", PersistentDataType.STRING]!!
            val first = index.split("-")[0].toInt()
            val last = index.split("-")[1].toInt()
            lore = lore!!.subList(first, last)

            remove("display_mark")
            remove("enchants_serialized")
            remove("lore_index")
        }
    }
}