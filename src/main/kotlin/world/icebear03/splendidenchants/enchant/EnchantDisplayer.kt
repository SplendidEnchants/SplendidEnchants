@file:Suppress("deprecation")

package world.icebear03.splendidenchants.enchant

import org.bukkit.GameMode
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import taboolib.common.util.replaceWithOrder
import world.icebear03.splendidenchants.api.EnchantAPI
import world.icebear03.splendidenchants.api.ItemAPI
import world.icebear03.splendidenchants.enchant.data.Rarity
import world.icebear03.splendidenchants.util.YamlUpdater
import kotlin.math.min

object EnchantDisplayer {

    //物品已经处理过了
    var displayMarkKey: NamespacedKey = NamespacedKey("splendidenchants", "display_mark")

    //物品lore中原来的部分的下标(String "firstIndex-lastIndex")
    val loreIndexKey: NamespacedKey = NamespacedKey("splendidenchants", "lore_index")

    //物品上附魔的序列化，便于在创造模式的还原(Map->String  "enchant_1_name:1|enchant_2_name:3……")
    val itemEnchantKey: NamespacedKey = NamespacedKey("splendidenchants", "item_enchants")

    var defaultPrevious: String
    var defaultSubsequent: String

    var sortByLevel: Boolean
    var rarityOrder: List<String>

    var combine: Boolean
    var minimal: Int
    var amount: Int
    var layouts: List<String>

    var loreFormation = mutableMapOf<Boolean, List<String>>()

    init {
        val config = YamlUpdater.loadAndUpdate("enchants/display.yml")
        defaultPrevious = config.getString("format.default_previous", "{color}{name} {roman_level}")!!
        defaultSubsequent = config.getString("format.default_subsequent", "\n§8| §7{description}")!!
        sortByLevel = config.getBoolean("sort.level", true)
        rarityOrder = config.getStringList("sort.rarity.order")
        Rarity.rarities.keys.forEach {
            if (!rarityOrder.contains(it))
                rarityOrder += it
        }

        combine = config.getBoolean("combine.enable", true)
        minimal = config.getInt("combine.min", 8)
        amount = config.getInt("combine.amount", 2)
        layouts = config.getStringList("combine.layout")

        loreFormation[true] = config.getStringList("lore_formation.has_lore")
        loreFormation[false] = config.getStringList("lore_formation.without_lore")
    }

    //对附魔排序
    fun sortEnchants(enchants: Map<SplendidEnchant, Int>): LinkedHashMap<SplendidEnchant, Int> {
        val sorted = linkedMapOf<SplendidEnchant, Int>()
        enchants.toList().sortedBy {
            rarityOrder.indexOf(it.first.rarity.id) * 100000 +
                    (if (sortByLevel) it.second else 0)
        }.forEach {
            sorted[it.first] = it.second
        }
        return sorted
    }

    //插入附魔对应的lore
    fun generateEnchantLore(item: ItemStack?, player: Player?): List<String> {
        if (item == null) return listOf()

        val enchants = sortEnchants(ItemAPI.getEnchants(item))

        if (enchants.isEmpty()) return listOf()

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
                        ).toArray()
                    )
                }
                enchantLore += layout.split("\n")
            }
        }

        return enchantLore
    }

    //展示是给玩家看的，玩家必须存在
    fun display(item: ItemStack, player: Player): ItemStack {
        val clone = item.clone()
        val meta = clone.itemMeta
        val pdc = meta.persistentDataContainer

        //TODO 此处Mical未优化
        //若本来就不需要显示附魔，就不显示了
        //注意，附魔书对应的隐藏附魔flag是HIDE POTION EFFECTS而不是HIDE ENCHANTS（1.18-是这样，1.19+未知）
        if (ItemAPI.isBook(item)) {
            if (meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS) || meta.hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS))
                return item
            else {
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS)
            }
        } else {
            if (meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS))
                return item
            else {
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            }
        }

        //已经展示过了就重新展示（2.0遗留思路）（个人认为多余，可以尝试去除此处，节省性能）
        if (pdc.has(displayMarkKey, PersistentDataType.INTEGER)) {
            return clone
        }

        if (ItemAPI.getEnchants(clone).isEmpty()) {
            return clone
        }

        //上标记
        pdc.set(displayMarkKey, PersistentDataType.INTEGER, 1)

        //上lore
        val enchantLore = generateEnchantLore(item, player).toMutableList()
        val origin = meta.lore ?: emptyList()
        //TODO 此处未优化
        val lore = mutableListOf<String>()
        var first = 0
        var last = 0
        loreFormation[!origin.isEmpty()]!!.forEach {
            when (it) {
                "{enchant_lore}" -> lore += enchantLore
                "{item_lore}" -> {
                    first = lore.size
                    lore += origin
                    last = lore.size
                }

                else -> lore += it
            }
        }
        meta.lore = lore
        pdc.set(loreIndexKey, PersistentDataType.STRING, "$first-$last")

        //加附魔序列化数据
        var data = ""
        ItemAPI.getEnchants(item).forEach {
            data += it.key.basicData.name + ":" + it.value + "|"
        }
        pdc.set(itemEnchantKey, PersistentDataType.STRING, data)

        clone.itemMeta = meta

        return clone
    }

    fun undisplay(item: ItemStack, player: Player): ItemStack {
        val clone = item.clone()
        var meta: ItemMeta = clone.itemMeta ?: return clone

        val pdc = meta.persistentDataContainer

        if (!pdc.has(displayMarkKey, PersistentDataType.INTEGER)) {
            return item
        }
        meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS)
        if (ItemAPI.isBook(item)) {
            meta.removeItemFlags(ItemFlag.HIDE_POTION_EFFECTS)
        }

        //创造模式的额外处理，需要重新给物品附魔
        //这是因为创造模式下客户端会重新设置背包物品，而重新设置的物品中非原版附魔会消失
        //这是由于客户端没有注册更多附魔
        if (player.gameMode == GameMode.CREATIVE) {
            val enchantInfo = pdc.get(itemEnchantKey, PersistentDataType.STRING)!!
            //清除附魔
            ItemAPI.setEnchants(meta, mapOf())
            //重新添加附魔
            enchantInfo.split("|").forEach {
                if (it.contains(":")) {
                    val enchant = EnchantAPI.getSplendidEnchant(it.split(":")[0])
                    val level = it.split(":")[1].toInt()
                    if (enchant != null) {
                        meta = ItemAPI.addEnchant(meta, enchant, level)
                    }
                }
            }
        }

        //除去enchant lore
        if (meta.lore != null) {
            val indexInfo = pdc.get(loreIndexKey, PersistentDataType.STRING)!!
            val first = indexInfo.split("-")[0].toInt()
            val last = indexInfo.split("-")[1].toInt()
            if (meta.lore!!.size >= last) {
                meta.lore = meta.lore!!.subList(first, last)
            }
        }

        //除去PDC数据
        pdc.remove(displayMarkKey)
        pdc.remove(loreIndexKey)
        pdc.remove(itemEnchantKey)

        clone.itemMeta = meta

        return clone
    }
}