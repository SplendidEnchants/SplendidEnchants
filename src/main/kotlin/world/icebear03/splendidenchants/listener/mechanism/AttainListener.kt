package world.icebear03.splendidenchants.listener.mechanism

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.event.enchantment.PrepareItemEnchantEvent
import org.bukkit.event.world.LootGenerateEvent
import org.bukkit.inventory.ItemStack
import org.serverct.parrot.parrotx.function.round
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.console
import taboolib.common.platform.function.submit
import taboolib.common.util.replaceWithOrder
import taboolib.module.kether.compileToJexl
import world.icebear03.splendidenchants.api.EnchantAPI
import world.icebear03.splendidenchants.api.ItemAPI
import world.icebear03.splendidenchants.api.internal.YamlUpdater
import world.icebear03.splendidenchants.enchant.data.limitation.CheckType
import kotlin.math.roundToInt


object AttainListener {

    val shelfAmount = mutableMapOf<Location, Int>()

    var vanillaTable = false
    var moreEnchantChance = listOf("0.2*{cost_level}", "0.15*{cost_level}", "0.1*{cost_level}")
    var levelFormula = "{cost_level}/3*{max_level}+{cost_level}*({random}-{random})"
    var celebrateNotice = mutableMapOf<String, List<String>>()
    var moreEnchantPrivilege = mutableMapOf<String, String>()
    var fullLevelPrivilege = "splendidenchants.privilege.table.full"

    fun initialize() {
        val config = YamlUpdater.loadAndUpdate("mechanisms/enchanting_table.yml")
        vanillaTable = config.getBoolean("vanilla_table", false)
        moreEnchantChance = config.getStringList("more_enchant_chance")
        levelFormula = config.getString("level_formula", levelFormula)!!

        val section = config.getConfigurationSection("celebrate_notice")!!
        celebrateNotice.clear()
        section.getKeys(false).forEach {
            celebrateNotice[it] = section.getStringList(it)
        }

        moreEnchantPrivilege.clear()
        config.getStringList("privilege.chance").forEach {
            moreEnchantPrivilege[it.split(":")[0]] = it.split(":")[1]
        }
        fullLevelPrivilege = config.getString("privilege.full_level", fullLevelPrivilege)!!

        console().sendMessage("    Successfully load table & looting module")
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun event(event: LootGenerateEvent) {
        if (event.entity != null) {
            val items = mutableListOf<ItemStack>()
            items.addAll(event.loot)
            event.loot.clear()

            items.forEach { item ->
                if (ItemAPI.getEnchants(item).isNotEmpty()) {
                    val newItem = ItemAPI.clearEnchants(item)
                    event.loot.add(ItemAPI.setEnchants(newItem, enchantToAdd(event.entity as Player, newItem).first))
                } else {
                    event.loot.add(item)
                }
            }
        } else {
            event.loot.removeIf {
                ItemAPI.getEnchants(it).isNotEmpty()
            }
        }
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
        val bonus = shelfAmount[event.enchantBlock.location] ?: 16

        val result = enchantToAdd(player, item, costLevel, bonus)
        event.enchantsToAdd.putAll(result.first)

        //对书的附魔，必须手动进行，因为原版处理会掉特殊附魔
        if (item.type == Material.BOOK) {
            event.enchantsToAdd.clear()
            submit {
                val book = event.inventory.getItem(0)!!
                book.type = Material.ENCHANTED_BOOK
                ItemAPI.setEnchants(book, event.enchantsToAdd)
            }
        }
    }

    fun enchantToAdd(
        player: Player,
        item: ItemStack,
        costLevel: Int = 3,
        bonus: Int = 16
    ): Pair<Map<Enchantment, Int>, ItemStack> {
        val resultMap = mutableMapOf<Enchantment, Int>()
        val resultItem = item.clone()

        val amount = enchantAmount(player, costLevel)
        val availableEnchants = EnchantAPI.getAvailableEnchants(player, resultItem, CheckType.ATTAIN)

        for (i in 0 until amount) {
            val enchant = EnchantAPI.drawInRandom(availableEnchants) ?: break
            val maxLevel = enchant.maxLevel

            val level = if (player.hasPermission(fullLevelPrivilege)) {
                maxLevel
            } else {
                maxOf(
                    1, minOf(
                        maxLevel,
                        (levelFormula.replaceWithOrder(
                            bonus.toString() to "bonus",
                            maxLevel.toString() to "max_level",
                            costLevel.toString() to "cost_level",
                            Math.random().round(3).toString() to "random"
                        )
                            .compileToJexl().eval() as Double
                                ).roundToInt()
                    )
                )
            }

            if (enchant.limitations.checkAvailable(resultItem).first) {
                resultMap[enchant] = level
                ItemAPI.addEnchant(resultItem, enchant, level)
            }
        }

        return resultMap to resultItem
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