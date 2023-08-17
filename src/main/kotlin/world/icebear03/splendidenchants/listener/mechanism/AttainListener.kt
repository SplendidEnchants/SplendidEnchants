package world.icebear03.splendidenchants.listener.mechanism

import org.bukkit.Material
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
import world.icebear03.splendidenchants.api.*
import world.icebear03.splendidenchants.api.internal.YamlUpdater
import world.icebear03.splendidenchants.enchant.SplendidEnchant
import world.icebear03.splendidenchants.enchant.data.limitation.CheckType
import kotlin.math.roundToInt


object AttainListener {

    val shelfAmount = mutableMapOf<String, Int>()

    var vanillaTable = false
    var moreEnchantChance = listOf("0.2*{cost}", "0.15*{cost}", "0.1*{cost}")
    var levelFormula = "{cost}/3*{max_level}+{cost}*({random}-{random})"
    var celebrateNotice = mutableMapOf<String, List<String>>()
    var moreEnchantPrivilege = mutableMapOf<String, String>()
    var fullLevelPrivilege = "splendidenchants.privilege.table.full"

    fun load() {
        YamlUpdater.loadAndUpdate("mechanisms/enchanting_table.yml").run {
            vanillaTable = getBoolean("vanilla_table", false)
            moreEnchantChance = getStringList("more_enchant_chance")
            levelFormula = getString("level_formula", levelFormula)!!

            val section = getConfigurationSection("celebrate_notice")!!
            celebrateNotice.clear()
            celebrateNotice.putAll(section.getKeys(false).map { it to section.getStringList(it) })

            moreEnchantPrivilege.clear()
            moreEnchantPrivilege.putAll(getStringList("privilege.chance").map { it.split(":")[0] to it.split(":")[1] })
            fullLevelPrivilege = getString("privilege.full_level", fullLevelPrivilege)!!
        }

        console().sendMessage("    Successfully load table & looting module")
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun loot(event: LootGenerateEvent) {
        (event.entity as? Player)?.let {
            event.loot.replaceAll { item ->
                if (item.fixedEnchants.isNotEmpty()) enchant(it, ItemStack(item.type)).second
                else item
            }
        } ?: event.loot.removeIf { it.fixedEnchants.isNotEmpty() }
    }

    //记录附魔台的书架等级
    @SubscribeEvent(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun prepareEnchant(event: PrepareItemEnchantEvent) {
        if (vanillaTable)
            return
        shelfAmount[event.enchantBlock.location.serialized] = event.enchantmentBonus.coerceAtMost(16)
    }

    @SubscribeEvent(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun enchant(event: EnchantItemEvent) {
        if (vanillaTable)
            return

        val player = event.enchanter
        val item = event.item.clone()
        val cost = event.whichButton() + 1
        val bonus = shelfAmount[event.enchantBlock.location.serialized] ?: 1

        val result = enchant(player, item, cost, bonus)

        result.first.ifEmpty {
            event.isCancelled = true
            return
        }

        event.enchantsToAdd.clear()
        event.enchantsToAdd.putAll(result.first)

        //对书的附魔，必须手动进行，因为原版处理会掉特殊附魔
        if (item.type == Material.BOOK) {
            submit {
                event.inventory.setItem(0, result.second)
            }
        }
    }

    fun enchant(
        player: Player,
        item: ItemStack,
        cost: Int = 3,
        bonus: Int = 16,
        checkType: CheckType = CheckType.ATTAIN
    ): Pair<Map<SplendidEnchant, Int>, ItemStack> {
        val enchantsToAdd = mutableMapOf<SplendidEnchant, Int>()
        val result = item.clone()
        if (item.type == Material.BOOK) result.type = Material.ENCHANTED_BOOK

        val amount = enchantAmount(player, cost)
        val pool = result.etsAvailable(checkType, player)

        repeat(amount) {
            val enchant = pool.drawEt() ?: return@repeat
            val maxLevel = enchant.maxLevel
            val level = if (player.hasPermission(fullLevelPrivilege)) maxLevel
            else levelFormula.calcToInt("bonus" to bonus, "max_level" to maxLevel, "cost" to cost, "random" to Math.random().round(3))
                .coerceAtLeast(1)
                .coerceAtMost(maxLevel)

            if (enchant.limitations.checkAvailable(checkType, result, player).first) {
                result.addEt(enchant)
                enchantsToAdd[enchant] = level
            }
        }

        return enchantsToAdd to result
    }

    fun enchantAmount(player: Player, cost: Int) = moreEnchantChance.count {
        Math.random() <= finalChance(it.calcToDouble("cost" to cost), player)
    }.coerceAtLeast(1)

    fun finalChance(origin: Double, player: Player) = moreEnchantPrivilege.maxOf { (perm, expression) ->
        if (player.hasPermission(perm)) expression.calcToInt("chance" to origin)
        else origin.roundToInt()
    }.coerceAtLeast(0)
}