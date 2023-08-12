package world.icebear03.splendidenchants

import taboolib.common.platform.Plugin
import taboolib.common.platform.function.console
import taboolib.module.chat.colored
import world.icebear03.splendidenchants.enchant.EnchantDisplayer
import world.icebear03.splendidenchants.enchant.EnchantGroup
import world.icebear03.splendidenchants.enchant.EnchantLoader
import world.icebear03.splendidenchants.enchant.data.Rarity
import world.icebear03.splendidenchants.enchant.data.Target
import world.icebear03.splendidenchants.listener.mechanism.*
import world.icebear03.splendidenchants.ui.*
import world.icebear03.splendidenchants.util.EnchantFilter

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
        EnchantDisplayer.initialize()
        EnchantLoader.initialize()
        EnchantGroup.load()

        console().sendMessage("|- Loading Mechanisms...")
        AnvilListener.initialize()
        AttainListener.initialize()
        GrindstoneListener.initialize()
        VillagerListener.initialize()
        ExpListener.initialize()

        console().sendMessage("|- Loading GUIs...")
        console().sendMessage("                            ")
        console().sendMessage("Installed SplendidEnchants in §6${System.currentTimeMillis() - stamp}ms")
        console().sendMessage("| Version: §r3.0.0")
        console().sendMessage("| Author: §f白熊_IceBear " + "&{#FFD0DB}Micalhl".colored() + " §fxiaozhangup")
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
        EnchantDisplayer.initialize()
        EnchantLoader.initialize(true)
        EnchantGroup.load()
        EnchantFilter.clearAll()

        console().sendMessage("|- Reloading Mechanisms...")
        AnvilListener.initialize()
        AttainListener.initialize()
        GrindstoneListener.initialize()
        VillagerListener.initialize()
        ExpListener.initialize()

        console().sendMessage("|- Reloading GUIs...")
        MainMenuUI.reload()
        ItemCheckUI.reload()
        EnchantSearchUI.reload()
        FilterRarityUI.reload()
        FilterTargetUI.reload()
        FilterGroupUI.reload()
        AnvilUI.reload()

        console().sendMessage("                            ")
        console().sendMessage("Reloaded SplendidEnchants in §6${System.currentTimeMillis() - stamp}ms")
        console().sendMessage("| Version: §r3.0.0")
        console().sendMessage("| Author: §f白熊_IceBear " + "&{#FFD0DB}Micalhl".colored() + " §fxiaozhangup")
    }
}