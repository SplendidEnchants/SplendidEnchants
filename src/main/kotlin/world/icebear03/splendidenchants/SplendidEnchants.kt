package world.icebear03.splendidenchants

import org.serverct.parrot.parrotx.mechanism.Reloadables
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.console
import taboolib.module.chat.colored
import taboolib.platform.util.onlinePlayers
import world.icebear03.splendidenchants.api.internal.FurtherOperation
import world.icebear03.splendidenchants.enchant.EnchantDisplayer
import world.icebear03.splendidenchants.enchant.EnchantFilter
import world.icebear03.splendidenchants.enchant.EnchantLoader
import world.icebear03.splendidenchants.enchant.data.Group
import world.icebear03.splendidenchants.enchant.data.Rarity
import world.icebear03.splendidenchants.enchant.data.Target
import world.icebear03.splendidenchants.listener.mechanism.*

object SplendidEnchants : Plugin() {

    override fun onEnable() {
        val stamp = System.currentTimeMillis()
        console().sendMessage("§e                                                               ")
        console().sendMessage("§e ______   ______  __       ______   __   __   _____    __   _____                         ")
        console().sendMessage("§e/\\  ___\\ /\\  == \\/\\ \\     /\\  ___\\ /\\ \"-.\\ \\ /\\  __-. /\\ \\ /\\  __-.                       ")
        console().sendMessage("§e\\ \\___  \\\\ \\  _-/\\ \\ \\____\\ \\  __\\ \\ \\ \\-.  \\\\ \\ \\/\\ \\\\ \\ \\\\ \\ \\/\\ \\                      ")
        console().sendMessage("§e \\/\\_____\\\\ \\_\\   \\ \\_____\\\\ \\_____\\\\ \\_\\\\\"\\_\\\\ \\____- \\ \\_\\\\ \\____-                      ")
        console().sendMessage("§e  \\/_____/ \\/_/    \\/_____/ \\/_____/ \\/_/ \\/_/ \\/____/  \\/_/ \\/____/                      ")
        console().sendMessage("§e                                                                                          ")
        console().sendMessage("§e                ______   __   __   ______   __  __   ______   __   __   ______  ______    ")
        console().sendMessage("§e               /\\  ___\\ /\\ \"-.\\ \\ /\\  ___\\ /\\ \\_\\ \\ /\\  __ \\ /\\ \"-.\\ \\ /\\__  _\\/\\  ___\\   ")
        console().sendMessage("§e               \\ \\  __\\ \\ \\ \\-.  \\\\ \\ \\____\\ \\  __ \\\\ \\  __ \\\\ \\ \\-.  \\\\/_/\\ \\/\\ \\___  \\  ")
        console().sendMessage("§e                \\ \\_____\\\\ \\_\\\\\"\\_\\\\ \\_____\\\\ \\_\\ \\_\\\\ \\_\\ \\_\\\\ \\_\\\\\"\\_\\  \\ \\_\\ \\/\\_____\\ ")
        console().sendMessage("§e                 \\/_____/ \\/_/ \\/_/ \\/_____/ \\/_/\\/_/ \\/_/\\/_/ \\/_/ \\/_/   \\/_/  \\/_____/ ")
        console().sendMessage("§e                                                               ")
        console().sendMessage("Installing SplendidEnchants...")

        console().sendMessage("|- Loading Config Module...")
        Config.initialize()

        console().sendMessage("|- Loading Enchants...")
        Rarity.load()
        Target.load()
        EnchantDisplayer.load()
        EnchantLoader.load()
        Group.load()

        console().sendMessage("|- Loading Mechanisms...")
        AnvilListener.initialize()
        AttainListener.initialize()
        GrindstoneListener.initialize()
        VillagerListener.initialize()
        ExpListener.initialize()

        FurtherOperation.load()

        console().sendMessage("|- Loading GUIs...")
        Reloadables.execute()

        console().sendMessage("                            ")
        console().sendMessage("Installed SplendidEnchants in §6${System.currentTimeMillis() - stamp}ms")
        console().sendMessage("| Version: §r3.0.0")
        console().sendMessage("| Author: §f白熊_IceBear " + "&{#FFD0DB}Micalhl".colored() + " §fxiaozhangup")
        console().sendMessage("| Tester: All players of §bStarLight§3繁星工坊")
    }

    fun reload() {
        val stamp = System.currentTimeMillis()
        console().sendMessage("Reloading SplendidEnchants...")

        console().sendMessage("|- Reloading Config Module...")
        Config.initialize()

        console().sendMessage("|- Reloading Enchants...")
        EnchantLoader.resetSort()
        Rarity.load()
        Target.load()
        EnchantDisplayer.load()
        EnchantLoader.load(true)
        Group.load()
        onlinePlayers.forEach { EnchantFilter.clearFilters(it) }

        console().sendMessage("|- Reloading Mechanisms...")
        AnvilListener.initialize()
        AttainListener.initialize()
        GrindstoneListener.initialize()
        VillagerListener.initialize()
        ExpListener.initialize()

        console().sendMessage("|- Reloading GUIs...")
        Reloadables.execute()

        console().sendMessage("                            ")
        console().sendMessage("Reloaded SplendidEnchants in §6${System.currentTimeMillis() - stamp}ms")
        console().sendMessage("| Version: §r3.0.0")
        console().sendMessage("| Author: §f白熊_IceBear " + "&{#FFD0DB}Micalhl".colored() + " §fxiaozhangup")
        console().sendMessage("| Tester: All players of §bStarLight§3繁星工坊")
    }
}