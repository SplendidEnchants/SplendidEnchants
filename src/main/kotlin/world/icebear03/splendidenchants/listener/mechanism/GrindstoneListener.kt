package world.icebear03.splendidenchants.listener.mechanism

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent
import org.bukkit.entity.ExperienceOrb
import org.bukkit.entity.Player
import org.bukkit.event.inventory.PrepareGrindstoneEvent
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.console
import world.icebear03.splendidenchants.api.*
import world.icebear03.splendidenchants.api.internal.YamlUpdater
import world.icebear03.splendidenchants.enchant.data.isIn
import java.util.*
import kotlin.math.roundToInt


object GrindstoneListener {

    var enableVanilla = false
    var enableCustomGrindstone = true
    var expPerEnchant = "30*{level}/{max_level}*{rarity_bonus}"
    var accumulation = true
    var rarityBonus = mutableMapOf<String, Double>()
    var defaultBonus = 1.0
    var blacklist = "不可磨砂类附魔"
    var privilege = mutableMapOf<String, String>()

    fun load() {
        YamlUpdater.loadAndUpdate("mechanisms/grindstone.yml").run {
            enableVanilla = getBoolean("grindstone.vanilla", false)
            enableCustomGrindstone = getBoolean("grindstone.custom", true)

            expPerEnchant = getString("exp_per_enchant", expPerEnchant)!!
            accumulation = getBoolean("accumulation", true)
            val section = getConfigurationSection("rarity_bonus")!!
            rarityBonus.clear()
            rarityBonus.putAll(section.getKeys(false).map { it to section.getDouble(it) })

            defaultBonus = getDouble("default_bonus", 1.0)
            blacklist = getString("blacklist_group", blacklist)!!

            privilege.clear()
            AnvilListener.privilege.putAll(getStringList("privilege").map { it.split(":")[0] to it.split(":")[1] })
        }

        console().sendMessage("    Successfully load grindstone module")
    }


    val grindstoning = mutableMapOf<UUID, Int>()

    @SubscribeEvent(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun grindstone(event: PrepareGrindstoneEvent) {
        val inv = event.inventory
        val player = (event.viewers.getOrNull(0) ?: return) as Player
        val upper = inv.upperItem
        val lower = inv.lowerItem
        var exp = 0
        val result = event.result?.clone() ?: return

        result.clearEts()
        grind(player, upper)?.let { (item, refund) ->
            item.fixedEnchants.forEach { (enchant, level) -> result.addEt(enchant, level) }
            exp += refund
        }
        grind(player, lower)?.let { (item, refund) ->
            item.fixedEnchants.forEach { (enchant, level) -> result.addEt(enchant, level) }
            exp += refund
        }
        grindstoning[player.uniqueId] = exp
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun exp(event: PlayerPickupExperienceEvent) {
        val orb = event.experienceOrb
        val uuid = orb.triggerEntityId ?: return
        if (orb.spawnReason != ExperienceOrb.SpawnReason.GRINDSTONE) return
        orb.experience = grindstoning[uuid] ?: run { orb.remove(); event.isCancelled = true;return }
    }

    fun grind(player: Player, item: ItemStack?): Pair<ItemStack, Int>? {
        var total = 0.0
        val result = item?.clone() ?: return null
        result.clearEts()
        item.fixedEnchants.forEach { (enchant, level) ->
            val maxLevel = enchant.maxLevel
            if (enchant.isIn(blacklist)) result.addEt(enchant, level)
            else {
                val bonus = rarityBonus[enchant.rarity.id] ?: rarityBonus[enchant.rarity.name] ?: defaultBonus
                val refund = expPerEnchant.calcToDouble("level" to level, "max_level" to maxLevel, "bonus" to bonus)
                if (accumulation) total += refund
                else total = maxOf(total, refund)
            }
        }

        return result to finalRefund(total, player)
    }

    fun finalRefund(origin: Double, player: Player) = privilege.maxOf { (perm, expression) ->
        if (player.hasPermission(perm)) expression.calcToInt("refund" to origin)
        else origin.roundToInt()
    }.coerceAtLeast(0)
}