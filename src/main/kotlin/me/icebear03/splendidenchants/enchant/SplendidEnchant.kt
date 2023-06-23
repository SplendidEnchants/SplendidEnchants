package me.icebear03.splendidenchants.enchant

import io.papermc.paper.enchantments.EnchantmentRarity
import me.icebear03.splendidenchants.api.ItemAPI
import me.icebear03.splendidenchants.api.MathAPI
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
import taboolib.module.configuration.util.asMap
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
        val config = Configuration.loadFromFile(file)
        basicData = BasicData(config.getConfigurationSection("basic")!!)
        rarity = Rarity.fromIdOrName(config.getString("rarity", "null")!!)
        targets = arrayListOf()
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

    inner class Displayer(displayerConfig: ConfigurationSection) {

        val previousFormat = displayerConfig.getString("format.previous", "{default_previous}")!!
        val subsequentFormat = displayerConfig.getString("format.subsequent", "{default_subsequent}")!!
        private val generalDescription = displayerConfig.getString("description.general")!!
        private val specificDescription =
            if (displayerConfig.contains("description.specific"))
                displayerConfig.getString("description.specific")!!
            else generalDescription

        fun getSpecificDescription(replaceMap: List<Pair<String, String>>): String {
            return specificDescription.replaceWithOrder(replaceMap)
        }

        fun getSpecificFormat(level: Int, player: Player?, item: ItemStack?): String {
            val tmp = variable.generateReplaceMap(level, player, item).toMutableList()
            val l = (level ?: basicData.maxLevel)
            tmp += "{id}" to basicData.id
            tmp += "{name}" to basicData.name
            tmp += "{level}" to "" + level
            tmp += "{roman_level}" to MathAPI.numToRoman(level, true)
            tmp += "{max_level}" to "" + basicData.maxLevel
            tmp += "{color}" to rarity.color
            tmp += "{rarity}" to rarity.name
            tmp += "{description}" to getSpecificDescription(tmp)
            return (previousFormat.replace("{default_previous}", "TODO")
                    + subsequentFormat.replace("{default_subsequent}", "TODO")
                    ).replaceWithOrder(tmp)
        }
    }

    inner class Variable(variableConfig: ConfigurationSection?) {

        //所有变量 变量名 - 类型(leveled,player_related,modifiable)
        val variableSet = ConcurrentHashMap<String, String>()

        //变量名 - 公式
        private val leveled = ConcurrentHashMap<String, String>()

        //变量名 - PAPI变量
        private val playerRelated = ConcurrentHashMap<String, String>()

        //变量名 - 初始值
        private val modifiable = ConcurrentHashMap<String, Pair<String, String>>()

        init {
            variableConfig?.run {
                getConfigurationSection("leveled").asMap().forEach { (key, value) ->
                    leveled[key] = value as String
                    variableSet[key] = "leveled"
                }
                getConfigurationSection("player_related").asMap().forEach { (key, value) ->
                    playerRelated[key] = value as String
                    variableSet[key] = "player_related"
                }
                getConfigurationSection("modifiable").asMap().forEach { (key, value) ->
                    val parts = (value as String).split('=')
                    modifiable[key] = parts[0] to parts[1]
                    variableSet[key] = "modifiable"
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
            return if (item == null) variable else ItemAPI.getItemData(
                item,
                modifiable[variable]!!.first,
                PersistentDataType.STRING
            )
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
            config?.run {
                grindstoneable = getBoolean("grindstoneable", true)
                weight = getDouble("weight", 1.0)
                isTreasure = getBoolean("is_treasure", false)
                isCursed = getBoolean("is_cursed", false)
                isTradeable = getBoolean("is_tradeable", true)
                isDiscoverable = getBoolean("is_discoverable", true)
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
            maxLevel = config.getInt("max_level")
            key = NamespacedKey.fromString(id, null) ?: error("minecraft")
        }
    }
}