package world.icebear03.splendidenchants.enchant

import io.papermc.paper.enchantments.EnchantmentRarity
import net.kyori.adventure.text.Component
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.enchantments.EnchantmentTarget
import org.bukkit.entity.EntityCategory
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import taboolib.common5.cdouble
import taboolib.common5.cint
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.util.asMap
import taboolib.module.kether.isInt
import taboolib.platform.compat.replacePlaceholder
import taboolib.platform.util.modifyMeta
import world.icebear03.splendidenchants.api.*
import world.icebear03.splendidenchants.api.internal.colorify
import world.icebear03.splendidenchants.api.internal.exception.missingConfig
import world.icebear03.splendidenchants.enchant.data.Rarity
import world.icebear03.splendidenchants.enchant.data.Target
import world.icebear03.splendidenchants.enchant.data.limitation.CheckType
import world.icebear03.splendidenchants.enchant.data.limitation.Limitations
import world.icebear03.splendidenchants.enchant.data.rarity
import world.icebear03.splendidenchants.enchant.data.target
import world.icebear03.splendidenchants.enchant.mechanism.Listeners
import world.icebear03.splendidenchants.enchant.mechanism.Tickers
import world.icebear03.splendidenchants.enchant.mechanism.entry.internal.*
import java.io.File

class SplendidEnchant(file: File, key: NamespacedKey) : Enchantment(key) {

    var config: Config

    //基础信息 ID 显示名 最大等级 是否启用
    lateinit var basicData: BasicData

    //品质
    lateinit var rarity: Rarity

    //附魔对象
    lateinit var targets: List<Target>

    //限制，如冲突，依赖，权限
    lateinit var limitations: Limitations

    //附魔显示策略
    lateinit var displayer: Displayer

    //可选的属性
    lateinit var alternativeData: AlternativeData

    //变量 随等级而变 / 物品上的变量 / 玩家的变量
    lateinit var variable: Variable

    //自定义脚本
    lateinit var listeners: Listeners
    lateinit var tickers: Tickers

    init {
        config = Config(file)
        config.load()?.let { missingConfig(file, it) }
    }

    override fun hashCode(): Int = basicData.id.hashCode()

    override fun equals(other: Any?): Boolean = hashCode() == other.hashCode()

    override fun translationKey(): String = basicData.id

    @Deprecated(message = "Deprecated", replaceWith = ReplaceWith("basicData.id"))
    override fun getName(): String = basicData.id

    override fun getMaxLevel(): Int = basicData.maxLevel

    override fun getStartLevel(): Int = 1

    override fun getItemTarget(): EnchantmentTarget = EnchantmentTarget.ALL

    override fun isTreasure(): Boolean = alternativeData.isTreasure

    override fun isCursed(): Boolean = alternativeData.isCursed

    override fun conflictsWith(enchant: Enchantment) = false

    //支持了粘液的附魔机
    override fun canEnchantItem(item: ItemStack) = limitations.checkAvailable(CheckType.ANVIL, item).first

    override fun displayName(level: Int): Component = Component.text(basicData.name)

    override fun isTradeable(): Boolean = alternativeData.isTradeable

    override fun isDiscoverable(): Boolean = alternativeData.isDiscoverable

    override fun getRarity(): EnchantmentRarity = EnchantmentRarity.COMMON

    override fun getDamageIncrease(level: Int, entityCategory: EntityCategory): Float = 0.0f

    override fun getActiveSlots(): MutableSet<EquipmentSlot> = hashSetOf()

    inner class Config(file: File) {

        var file: File
        var config: Configuration

        init {
            this.file = file
            config = Configuration.loadFromFile(file)
        }

        fun modify(path: String, value: Any?) {
            config[path] = value
            config.saveToFile(file)
        }

        fun load(): String? {
            config = Configuration.loadFromFile(file)
            basicData = BasicData(config.getConfigurationSection("basic") ?: return "basic")
            rarity = rarity(config.getString("rarity")) ?: return "rarity"
            targets = config.getStringList("targets").mapNotNull { target(it) }
            limitations = Limitations(this@SplendidEnchant, config.getStringList("limitations"))
            displayer = Displayer(config.getConfigurationSection("display") ?: return "displayer")
            alternativeData = AlternativeData(config.getConfigurationSection("alternative"))
            variable = Variable(config.getConfigurationSection("variables"))
            listeners = Listeners(this@SplendidEnchant, config.getConfigurationSection("mechanisms.listeners"))
            tickers = Tickers(this@SplendidEnchant, config.getConfigurationSection("mechanisms.tickers"))
            return null
        }
    }

    inner class Displayer(displayerConfig: ConfigurationSection) {

        val previous = displayerConfig.getString("format.previous", "{default_previous}")!!
        val subsequent = displayerConfig.getString("format.subsequent", "{default_subsequent}")!!
        val generalDescription = displayerConfig.getString("description.general", "§7附魔介绍未设置")!!
        val specificDescription = displayerConfig.getString("description.specific", generalDescription)!!

        //生成本附魔在当前状态下的显示，在非合并模式下
        fun display(level: Int?, player: Player?, item: ItemStack?) = display(holders(level, player, item))

        //生成本附魔在当前状态下的显示，在非合并模式下
        fun display(holders: Map<String, String>): String {
            return (previous.replace("{default_previous}", EnchantDisplayer.defaultPrevious)
                    + subsequent.replace("{default_subsequent}", EnchantDisplayer.defaultSubsequent)
                    ).replace(holders).colorify()
        }

        //生成本附魔在当前状态下的显示，在合并模式下
        fun displays(
            level: Int? = null,
            player: Player? = null,
            item: ItemStack? = null,
            index: Int? = null
        ): Map<String, String> {
            val suffix = index?.let { "_$it" } ?: ""
            val holders = holders(level, player, item)
            return mapOf(
                "previous$suffix" to previous.replace("{default_previous}", EnchantDisplayer.defaultPrevious).replace(holders).colorify(),
                "subsequent$suffix" to subsequent.replace("{default_subsequent}", EnchantDisplayer.defaultSubsequent).replace(holders).colorify()
            )
        }

        //生成本附魔在当前状态下的变量替换map
        fun holders(
            level: Int? = null,
            player: Player? = null,
            item: ItemStack? = null
        ): Map<String, String> {
            val tmp = variable.variables(level, player, item, true).toMutableMap()
            val lv = level ?: basicData.maxLevel
            tmp["id"] = basicData.id
            tmp["name"] = basicData.name
            tmp["level"] = "$lv"
            tmp["roman_level"] = lv.roman(maxLevel == 1)
            tmp["roman_level_with_a_blank"] = lv.roman(maxLevel == 1, true)
            tmp["max_level"] = "${basicData.maxLevel}"
            tmp["rarity"] = rarity.name
            tmp["rarity_display"] = rarity.display()
            tmp["enchant_display"] = display()
            tmp["enchant_display_roman"] = display(level)
            tmp["enchant_display_lore"] = displayer.display(tmp)
            tmp["description"] = specificDescription.replace(tmp).colorify()
            return tmp
        }
    }

    inner class Variable(variableConfig: ConfigurationSection?) {

        //所有变量 变量名 - 类型(leveled,player_related,modifiable)
        val variables = mutableMapOf<String, VariableType>()

        //变量名 - 公式
        // value： map<等级，公式/数据> to 单位
        val leveled = mutableMapOf<String, Pair<String, LinkedHashMap<Int, String>>>()

        //变量名 - PAPI变量
        val playerRelated = mutableMapOf<String, String>()

        //变量名 - 初始值
        val modifiable = mutableMapOf<String, Pair<String, String>>()

        //变量名 - 类型 to 初始值
        val flexible = mutableMapOf<String, Pair<ObjectEntry<*>, String>>()

        init {
            variableConfig?.run {
                getConfigurationSection("leveled").asMap().forEach { (variable, any) ->
                    if (any is ConfigurationSection) {
                        val map = linkedMapOf<Int, String>()
                        any.asMap().mapKeys { it.key.cint }.mapValues { it.value.toString() }.toSortedMap().forEach {
                            map[it.key] = it.value
                        }
                        leveled[variable] = any.getString("unit", "单位")!! to map
                    } else {
                        val unit = any.toString().split(":")[0]
                        val formula = any.toString().split(":")[1]
                        leveled[variable] = unit to linkedMapOf(1 to formula)
                    }
                    variables[variable] = VariableType.LEVELED
                }
                getConfigurationSection("player_related").asMap().forEach { (variable, expression) ->
                    playerRelated[variable] = expression.toString()
                    variables[variable] = VariableType.PLAYER_RELATED
                }
                getConfigurationSection("modifiable").asMap().forEach { (variable, expression) ->
                    val parts = expression.toString().split('=')
                    modifiable[variable] = parts[0] to parts[1]
                    variables[variable] = VariableType.MODIFIABLE
                }
                getConfigurationSection("flexible").asMap().forEach { (variable, expression) ->
                    val type = when (expression.toString().split("::")[0]) {
                        "block" -> objBlock
                        "entity" -> objEntity
                        "living_entity" -> objLivingEntity
                        "player" -> objPlayer
                        "item" -> objItem
                        "vector" -> objVector
                        else -> objString
                    }
                    val init = expression.toString().split("::")[1]
                    flexible[variable] = type to init
                    variables[variable] = VariableType.FLEXIBLE
                }
            }
        }

        fun leveled(variable: String, level: Int?, withUnit: Boolean): String {
            return level?.let {
                val v = leveled[variable]!!
                v.second
                    .filter { it.key <= level }
                    .minBy { level - it.key }.value
                    .calculate("level" to level) + if (withUnit) v.first else ""
            } ?: "?"
        }

        fun playerRelated(variable: String, player: Player?): String = player?.let { playerRelated[variable]!!.replacePlaceholder(player) } ?: "?"

        fun modifiable(variable: String, item: ItemStack?): String {
            val pair = modifiable[variable]!!
            return item?.let { it.itemMeta["splendidenchant_" + pair.first, PersistentDataType.STRING] ?: pair.second } ?: "?"
        }

        private fun flexible(variable: String): Number {
            val init = flexible[variable]!!
            return if (init.isInt()) init.cint
            else init.cdouble
        }

        fun variables(
            level: Int?,
            entity: LivingEntity? = null,
            item: ItemStack? = null,
            withUnit: Boolean
        ): Map<String, String> {
            return variables.mapNotNull { (variable, type) ->
                variable to when (type) {
                    VariableType.LEVELED -> leveled(variable, level, withUnit)
                    VariableType.PLAYER_RELATED -> playerRelated(variable, entity as? Player)
                    VariableType.MODIFIABLE -> modifiable(variable, item)
                    else -> return@mapNotNull null
                }
            }.toMap()
        }

        fun modifyVariable(item: ItemStack, variable: String, value: String): ItemStack = item.modifyMeta<ItemMeta> {
            this["splendidenchant_" + modifiable[variable]!!.first, PersistentDataType.STRING] = value
        }
    }

    enum class VariableType {
        LEVELED,
        PLAYER_RELATED,
        MODIFIABLE,
        FLEXIBLE
    }

    inner class AlternativeData(alternativeDataConfig: ConfigurationSection?) {

        var grindstoneable: Boolean = true
        var weight: Int = 100
        var isTreasure: Boolean = false
        var isCursed: Boolean = false
        var isTradeable: Boolean = true
        var isDiscoverable: Boolean = true

        init {
            alternativeDataConfig?.run {
                grindstoneable = getBoolean("grindstoneable", true)
                weight = getInt("weight", 100)
                isTreasure = getBoolean("is_treasure", false)
                isCursed = getBoolean("is_cursed", false)
                isTradeable = getBoolean("is_tradeable", true)
                isDiscoverable = getBoolean("is_discoverable", true)
            }
        }
    }

    inner class BasicData(basicDataConfig: ConfigurationSection) {

        var enable: Boolean
        var disableWorlds: List<String>
        var id: String
        var name: String
        var maxLevel: Int
        val key: NamespacedKey

        init {
            basicDataConfig.run {
                enable = getBoolean("enable", true)
                disableWorlds = getStringList("disable_worlds")
                id = getString("id") ?: missingConfig(config.file, "basic")
                this@BasicData.name = getString("name") ?: id
                maxLevel = getInt("max_level")
                key = NamespacedKey.minecraft(id)
            }
        }
    }
}