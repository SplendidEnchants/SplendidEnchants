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
        info("Loading SplendidEnchants...")

        Config.initialize()

        //ENCHANT
        Rarity.initialize()
        Target.initialize()
        EnchantDisplayer.initialize()
        EnchantLoader.initialize()
        EnchantGroup.initialize()

        //Mechanisms
        AnvilListener.initialize()
        AttainListener.initialize()
        GrindstoneListener.initialize()
        VillagerListener.initialize()
    }

    override fun onDisable() {
        EnchantLoader.unregister()
    }

    fun reload() {
        info("Reloading SplendidEnchants...")

        //ENCHANT
        EnchantLoader.resetSort()

        Rarity.initialize()
        Target.initialize()
        EnchantDisplayer.initialize()
        EnchantLoader.initialize(true)
        EnchantGroup.initialize()

        //GUI
        MainMenuUI.reload()
        ItemCheckUI.reload()
        EnchantSearchUI.reload()

        //Mechianisms
        AnvilListener.initialize()
        AttainListener.initialize()
        GrindstoneListener.initialize()
        VillagerListener.initialize()

        //filter重置，因为可能留下不存在的rarity
        EnchantFilter.clearAll()
    }
}