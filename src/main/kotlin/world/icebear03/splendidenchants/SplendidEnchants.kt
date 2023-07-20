package world.icebear03.splendidenchants

import taboolib.common.platform.Plugin
import taboolib.common.platform.function.info
import world.icebear03.splendidenchants.enchant.EnchantGroup
import world.icebear03.splendidenchants.enchant.EnchantLoader
import world.icebear03.splendidenchants.enchant.data.Rarity
import world.icebear03.splendidenchants.enchant.data.Target

object SplendidEnchants : Plugin() {

    override fun onEnable() {
        info("Loading SplendidEnchants...")

        Config.initialize()

        Rarity.initialize()
        Target.initialize()
        EnchantLoader.initialize()
        EnchantGroup.initialize()
    }

    override fun onDisable() {
        EnchantLoader.unregister()
    }
}