package world.icebear03.splendidenchants.listener.mechanism

import org.bukkit.event.entity.VillagerAcquireTradeEvent
import org.bukkit.inventory.MerchantRecipe
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.console
import world.icebear03.splendidenchants.api.EnchantAPI
import world.icebear03.splendidenchants.api.ItemAPI
import world.icebear03.splendidenchants.enchant.data.Group
import world.icebear03.splendidenchants.util.YamlUpdater

object VillagerListener {

    var enableEnchantTrade = true
    var tradeGroup = "可交易附魔"
    var amount = 2

    fun initialize() {
        val config = YamlUpdater.loadAndUpdate("mechanisms/villager.yml")
        enableEnchantTrade = config.getBoolean("enable", true)
        tradeGroup = config.getString("group", tradeGroup)!!
        amount = config.getInt("amount", 2)

        console().sendMessage("    Successfully load merchant module")
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun event(event: VillagerAcquireTradeEvent) {
        val result = event.recipe.result.clone()

        if (ItemAPI.getEnchants(result).isEmpty())
            return
        if (!enableEnchantTrade) {
            event.isCancelled = true
            return
        }

        val item = ItemAPI.clearEnchants(result)

        for (i in 0 until amount) {
            val enchant = EnchantAPI.drawInRandom(Group.getSplendidEnchants(tradeGroup)) ?: return
            ItemAPI.addEnchant(item, enchant, enchant.maxLevel)
        }

        val recipe = MerchantRecipe(
            item,
            event.recipe.uses,
            event.recipe.maxUses,
            event.recipe.hasExperienceReward(),
            event.recipe.villagerExperience,
            event.recipe.priceMultiplier
        )
        recipe.ingredients = event.recipe.ingredients
        event.recipe = recipe
    }
}