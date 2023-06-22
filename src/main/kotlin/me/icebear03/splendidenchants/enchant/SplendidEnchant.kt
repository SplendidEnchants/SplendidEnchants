package me.icebear03.splendidenchants.enchant

import io.papermc.paper.enchantments.EnchantmentRarity
import me.icebear03.splendidenchants.api.ItemAPI
import me.icebear03.splendidenchants.api.PlayerAPI
import me.icebear03.splendidenchants.enchant.data.Rarity
import me.icebear03.splendidenchants.enchant.data.Target
import me.icebear03.splendidenchants.enchant.data.limitation.Limitations
import net.kyori.adventure.text.Component
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.enchantments.EnchantmentTarget
import org.bukkit.entity.EntityCategory
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import taboolib.common.util.replaceWithOrder
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Configuration
import taboolib.module.kether.compileToJexl
import java.io.File
import java.util.concurrent.ConcurrentHashMap

class SplendidEnchant(file: File, key: NamespacedKey) : Enchantment(key) {

    var basicData: BasicData
    var rarity: Rarity
    var targets: List<Target>
    var limitations: Limitations
    var displayer: Displayer
    var alternativeData: AlternativeData
    var variable: Variable

    init {
        val config: Configuration = Configuration.loadFromFile(file)
        basicData = BasicData(config.getConfigurationSection("basic")!!)
        rarity = Rarity.fromIdOrName(config.getString("rarity", "null")!!)
        targets = ArrayList()
        config.getStringList("targets").forEach {
            targets += Target.fromIdOrName(it)
        }
        limitations = Limitations(this, config.getStringList("limitations"))
        displayer = Displayer(config.getConfigurationSection("display")!!)
        alternativeData = AlternativeData(config.getConfigurationSection("alternative"))
        variable = Variable(config.getConfigurationSection("variables"))
    }


    override fun translationKey(): String = basicData.id

    override fun getName(): String = basicData.id

    override fun getMaxLevel(): Int = basicData.maxLevel

    override fun getStartLevel(): Int = 1

    override fun getItemTarget(): EnchantmentTarget = EnchantmentTarget.ALL

    override fun isTreasure(): Boolean = alternativeData.isTreasure

    override fun isCursed(): Boolean = alternativeData.isCursed

    override fun conflictsWith(enchant: Enchantment): Boolean = limitations.conflictWith(enchant)

    override fun canEnchantItem(item: ItemStack): Boolean = limitations.checkAvailable(item).first

    override fun displayName(level: Int): Component = Component.text(basicData.name)

    override fun isTradeable(): Boolean = alternativeData.isTradeable

    override fun isDiscoverable(): Boolean = alternativeData.isDiscoverable

    override fun getRarity(): EnchantmentRarity = EnchantmentRarity.COMMON

    override fun getDamageIncrease(level: Int, entityCategory: EntityCategory): Float = 0.0f

    override fun getActiveSlots(): MutableSet<EquipmentSlot> = hashSetOf()

    inner class Displayer(val displayerConfig: ConfigurationSection) {

        val displayFormat = displayerConfig.getString("format")!!
        private val generalDescription = displayerConfig.getString("description.general")!!
        private val specificDescription =
            if (displayerConfig.contains("description.specific"))
                displayerConfig.getString("description.specific")!!
            else generalDescription

        fun getSpecificDescription(level: Int, player: Player?, item: ItemStack?): String {
            return specificDescription.replaceWithOrder(
                variable.generateReplaceMap(level, player, item)
                // 也可以为: this@SplendidEnchant.variable.generateReplaceMap(level, player, item)
            )
        }
        // TODO: 读取 display.yml 中的 $default
        // TODO: 根据不同等级、不同状态（TODO-ItemStack PDC储存）生成 display
    }

    inner class Variable(val variableConfig: ConfigurationSection?) {

        //所有变量 变量名 - 类型(leveled,player_related,modifiable)
        val variableSet = ConcurrentHashMap<String, String>()

        //变量名 - 公式
        private val leveled = ConcurrentHashMap<String, String>()

        //变量名 - PAPI变量
        private val playerRelated = ConcurrentHashMap<String, String>()

        //变量名 - 初始值
        private val modifiable = ConcurrentHashMap<String, Pair<String, String>>()

        init {
            if (variableConfig != null) {
                var section = variableConfig.getConfigurationSection("leveled")
                section?.getKeys(false)?.forEach {
                    leveled[it] = section!!.getString(it)!!
                    variableSet[it] = "leveled"
                }
                section = variableConfig.getConfigurationSection("player_related")
                section?.getKeys(false)?.forEach {
                    playerRelated[it] = section!!.getString(it)!!
                    variableSet[it] = "player_related"
                }
                section = variableConfig.getConfigurationSection("modifiable")
                section?.getKeys(false)?.forEach {
                    val tmp = section.getString(it)!!
                    modifiable[it] = tmp.split("=")[0] to tmp.split("=")[1]
                    variableSet[it] = "modifiable"
                }
            }
        }

        private fun leveled(variable: String, level: Int?): String {
            return if (level == null) variable else leveled[variable]!!.compileToJexl().eval(mapOf("level" to level))
                .toString()
        }

        private fun playerRelated(variable: String, player: Player?): String {
            return if (player == null) variable else PlayerAPI.convertPlaceHolders(variable, player)
        }

        private fun modifiable(variable: String, item: ItemStack?): String {
            if (item == null) return variable
            return ItemAPI.getItemData(item, modifiable[variable]!!.first, PersistentDataType.STRING)
                ?: modifiable[variable]!!.second
        }

        fun generateReplaceMap(level: Int?, player: Player?, item: ItemStack?): List<Pair<String, String>> {
            val list = arrayListOf<Pair<String, String>>()
            variableSet.forEach {
                when (it.value) {
                    "leveled" -> {
                        list.add(it.key to leveled(it.key, level))
                    }

                    "player_related" -> {
                        list.add(it.key to playerRelated(it.key, player))
                    }

                    "modifiable" -> {
                        list.add(it.key to modifiable(it.key, item))
                    }
                }
            }
            return list
        }
    }

    inner class AlternativeData(config: ConfigurationSection?) {

        var grindstoneable: Boolean = true
        var weight: Double = 1.0
        var isTreasure: Boolean = false
        var isCursed: Boolean = false
        var isTradeable: Boolean = true
        var isDiscoverable: Boolean = true

        init {
            if (config != null) {
                grindstoneable = config.getBoolean("grindstoneable", true)
                weight = config.getDouble("weight", 1.0)
                isTreasure = config.getBoolean("is_treasure", false)
                isCursed = config.getBoolean("is_cursed", false)
                isTradeable = config.getBoolean("is_tradeable", true)
                isDiscoverable = config.getBoolean("is_discoverable", true)
            }
        }
    }

    inner class BasicData(config: ConfigurationSection) {
        var id: String
        var name: String
        var maxLevel: Int
        private val key: NamespacedKey

        init {
            id = config.getString("id")!!
            name = config.getString("name")!!
            maxLevel = config.getInt("max_level")!!
            key = NamespacedKey.fromString(id, null) ?: error("minecraft")
        }
    }
}