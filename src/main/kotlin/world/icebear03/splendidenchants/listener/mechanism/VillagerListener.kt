package world.icebear03.splendidenchants.listener.mechanism

import org.bukkit.event.entity.VillagerAcquireTradeEvent
import org.bukkit.inventory.MerchantRecipe
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.console
import world.icebear03.splendidenchants.api.addEt
import world.icebear03.splendidenchants.api.clearEts
import world.icebear03.splendidenchants.api.drawEt
import world.icebear03.splendidenchants.api.fixedEnchants
import world.icebear03.splendidenchants.api.internal.YamlUpdater
import world.icebear03.splendidenchants.enchant.data.group

object VillagerListener {

    var enableEnchantTrade = true
    var tradeGroup = "可交易附魔"
    var amount = 2

    fun load() {
        YamlUpdater.loadAndUpdate("mechanisms/villager.yml").run {
            enableEnchantTrade = getBoolean("enable", true)
            tradeGroup = getString("group", tradeGroup)!!
            amount = getInt("amount", 2)
        }

        console().sendMessage("    Successfully load merchant module")
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun acquireTrade(event: VillagerAcquireTradeEvent) {
        val origin = event.recipe
        val result = origin.result.clone()

        if (result.fixedEnchants.isEmpty()) return
        if (!enableEnchantTrade) {
            event.isCancelled = true; return
        }

        result.clearEts()
        repeat(amount) { result.addEt((group(tradeGroup)?.enchants ?: listOf()).filter { it.alternativeData.isTradeable }.drawEt() ?: return@repeat) }

        origin.run origin@{
            event.recipe = MerchantRecipe(
                result, uses, maxUses, hasExperienceReward(), villagerExperience,
                priceMultiplier, demand, specialPrice, shouldIgnoreDiscounts()
            ).run new@{ this@new.ingredients = this@origin.ingredients; this }
        }
    }
}