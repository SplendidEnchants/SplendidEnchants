package world.icebear03.splendidenchants

import taboolib.common.platform.Plugin
import taboolib.common.platform.function.info
import world.icebear03.splendidenchants.enchant.EnchantDisplayer
import world.icebear03.splendidenchants.enchant.EnchantGroup
import world.icebear03.splendidenchants.enchant.EnchantLoader
import world.icebear03.splendidenchants.enchant.data.Rarity
import world.icebear03.splendidenchants.enchant.data.Target
import world.icebear03.splendidenchants.listener.mechanism.AnvilListener
import world.icebear03.splendidenchants.listener.mechanism.AttainListener
import world.icebear03.splendidenchants.listener.mechanism.GrindstoneListener
import world.icebear03.splendidenchants.listener.mechanism.VillagerListener
import world.icebear03.splendidenchants.ui.EnchantSearchUI
import world.icebear03.splendidenchants.ui.ItemCheckUI
import world.icebear03.splendidenchants.ui.MainMenuUI
import world.icebear03.splendidenchants.util.EnchantFilter

object SplendidEnchants : Plugin() {

    override fun onEnable() {
        val stamp = System.currentTimeMillis()
        info("                                                               ")
        info(" ______   ______  __       ______   __   __   _____    __   _____                         ")
        info("/\\  ___\\ /\\  == \\/\\ \\     /\\  ___\\ /\\ \"-.\\ \\ /\\  __-. /\\ \\ /\\  __-.                       ")
        info("\\ \\___  \\\\ \\  _-/\\ \\ \\____\\ \\  __\\ \\ \\ \\-.  \\\\ \\ \\/\\ \\\\ \\ \\\\ \\ \\/\\ \\                      ")
        info(" \\/\\_____\\\\ \\_\\   \\ \\_____\\\\ \\_____\\\\ \\_\\\\\"\\_\\\\ \\____- \\ \\_\\\\ \\____-                      ")
        info("  \\/_____/ \\/_/    \\/_____/ \\/_____/ \\/_/ \\/_/ \\/____/  \\/_/ \\/____/                      ")
        info("                                                                                          ")
        info("                ______   __   __   ______   __  __   ______   __   __   ______  ______    ")
        info("               /\\  ___\\ /\\ \"-.\\ \\ /\\  ___\\ /\\ \\_\\ \\ /\\  __ \\ /\\ \"-.\\ \\ /\\__  _\\/\\  ___\\   ")
        info("               \\ \\  __\\ \\ \\ \\-.  \\\\ \\ \\____\\ \\  __ \\\\ \\  __ \\\\ \\ \\-.  \\\\/_/\\ \\/\\ \\___  \\  ")
        info("                \\ \\_____\\\\ \\_\\\\\"\\_\\\\ \\_____\\\\ \\_\\ \\_\\\\ \\_\\ \\_\\\\ \\_\\\\\"\\_\\  \\ \\_\\ \\/\\_____\\ ")
        info("                 \\/_____/ \\/_/ \\/_/ \\/_____/ \\/_/\\/_/ \\/_/\\/_/ \\/_/ \\/_/   \\/_/  \\/_____/ ")
        info("                                                               ")
        info("Installing SplendidEnchants...")

        info("|- Loading Config Module...")
        Config.initialize()

        info("|- Loading Enchants...")
        Rarity.initialize()
        Target.initialize()
        EnchantDisplayer.initialize()
        EnchantLoader.initialize()
        EnchantGroup.initialize()

        info("|- Loading Mechanisms...")
        AnvilListener.initialize()
        AttainListener.initialize()
        GrindstoneListener.initialize()
        VillagerListener.initialize()

        info("|- Loading GUIs...")
        info("                            ")
        info("Installed SplendidEnchants in ${System.currentTimeMillis() - stamp}ms")
        info("| Version: 3.0.0")
        info("| Author: 白熊_IceBear Micalhl xiaozhangup")
    }

    fun reload() {
        val stamp = System.currentTimeMillis()
        info("Reloading SplendidEnchants...")

        info("|- Reloading Config Module...")
        Config.initialize()

        info("|- Reloading Enchants...")
        EnchantLoader.resetSort()
        Rarity.initialize()
        Target.initialize()
        EnchantDisplayer.initialize()
        EnchantLoader.initialize(true)
        EnchantGroup.initialize()
        EnchantFilter.clearAll()

        info("|- Reloading Mechanisms...")
        AnvilListener.initialize()
        AttainListener.initialize()
        GrindstoneListener.initialize()
        VillagerListener.initialize()

        info("|- Reloading GUIs...")
        MainMenuUI.reload()
        ItemCheckUI.reload()
        EnchantSearchUI.reload()

        info("                            ")
        info("Reloaded SplendidEnchants in ${System.currentTimeMillis() - stamp}ms")
        info("| Version: 3.0.0")
        info("| Author: 白熊_IceBear Micalhl xiaozhangup")
    }
}