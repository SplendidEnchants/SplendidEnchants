package world.icebear03.splendidenchants.listener.mechanism

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.event.enchantment.PrepareItemEnchantEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.module.kether.compileToJexl
import world.icebear03.splendidenchants.api.EnchantAPI
import world.icebear03.splendidenchants.api.ItemAPI
import world.icebear03.splendidenchants.enchant.data.limitation.CheckType
import world.icebear03.splendidenchants.util.YamlUpdater
import kotlin.math.roundToInt


object EnchantingTableListener {

    val vanillaTable: Boolean

    val shelfAmount = mutableMapOf<Location, Int>()

    val moreEnchantChance: MutableList<String>
    val levelFormula: String

    val celebrateNotice = mutableMapOf<String, List<String>>()

    val moreEnchantPrivilege = mutableMapOf<String, String>()
    val fullLevelPrivilege: String

    init {
        val config = YamlUpdater.loadAndUpdate("mechanisms/enchanting_table.yml")
        vanillaTable = config.getBoolean("vanilla_table", false)

        moreEnchantChance = config.getStringList("more_enchant_chance").toMutableList()
        levelFormula =
            config.getString("level_formula", "{cost_level}/3*{max_level}+{cost_level}*({random}-{random})")!!

        val section = config.getConfigurationSection("celebrate_notice")!!
        section.getKeys(false).forEach {
            celebrateNotice[it] = section.getStringList(it)
        }

        config.getStringList("privilege.chance").forEach { it ->
            moreEnchantPrivilege[it.split(":")[0]] = it.split(":")[1]
        }
        fullLevelPrivilege = config.getString("privilege.full_level", "splendidenchants.privilege.table.full")!!
    }

    //记录附魔台的书架等级
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun event(event: PrepareItemEnchantEvent) {
        if (vanillaTable)
            return
        shelfAmount[event.enchantBlock.location] = minOf(16, event.enchantmentBonus)
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun event(event: EnchantItemEvent) {
        if (vanillaTable)
            return
        event.enchantsToAdd.clear()
        val player = event.enchanter
        val item = event.item.clone()
        val costLevel = event.whichButton() + 1
        val amount = enchantAmount(player, costLevel)
        val bonus = shelfAmount[event.enchantBlock.location]

        val availableEnchants = EnchantAPI.getAvailableEnchants(player, item, CheckType.ATTAIN)
        for (i in 0 until amount) {
            val enchant = EnchantAPI.drawInRandom(availableEnchants) ?: break
            val maxLevel = enchant.maxLevel

            val level = if (player.hasPermission(fullLevelPrivilege)) {
                maxLevel
            } else {
                maxOf(
                    1, minOf(
                        maxLevel,
                        (levelFormula.replace("{bonus}", bonus.toString())
                            .replace("{max_level}", maxLevel.toString())
                            .replace("{cost_level}", costLevel.toString())
                            .compileToJexl().eval() as Double).roundToInt()
                    )
                )
            }

            if (enchant.limitations.checkAvailable(item).first) {
                event.enchantsToAdd[enchant] = level
                ItemAPI.addEnchant(item, enchant, level)
            }
        }

        //对书的附魔，必须手动进行，因为原版处理会掉特殊附魔
        if (item.type == Material.BOOK) {
            submit {
                val book = event.inventory.getItem(0)!!
                ItemAPI.setEnchants(book, event.enchantsToAdd)
                event.inventory.setItem(0, item)
            }
        }
    }

    fun enchantAmount(player: Player, costLevel: Int): Int {
        var result = 1
        moreEnchantChance.forEach {
            val chance = it.replace("{cost_level}", costLevel.toString()).compileToJexl().eval() as Double
            if (Math.random() <= finalChance(chance, player))
                result += 1
        }
        return result
    }

    fun finalChance(origin: Double, player: Player): Int {
        var maxChance = origin
        moreEnchantPrivilege.forEach {
            if (player.hasPermission(it.key)) {
                val newChance = it.value.replace("{chance}", origin.toString()).compileToJexl().eval() as Double
                maxChance = maxOf(maxChance, newChance)
            }
        }

        return maxOf(0, maxChance.roundToInt())
    }
}