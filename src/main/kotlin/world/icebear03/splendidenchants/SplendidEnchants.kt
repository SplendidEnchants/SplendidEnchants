package world.icebear03.splendidenchants

import taboolib.common.platform.Plugin
import taboolib.common.platform.function.info
import world.icebear03.splendidenchants.enchant.EnchantGroup
import world.icebear03.splendidenchants.enchant.EnchantLoader
import world.icebear03.splendidenchants.enchant.data.Rarity
import world.icebear03.splendidenchants.enchant.data.limitation.Target

object SplendidEnchants : Plugin() {

    override fun onEnable() {
        info("Loading SplendidEnchants...")

        Config.initialize()

        Rarity.initialize()
        Target.initialize()
        EnchantLoader.initialize()
        EnchantGroup.initialize()
//        println("3+7-a*9.5".compileToJexl().eval(mapOf("a" to 6)))
        // 注意需提前缓存好，建议在加载附魔的时候就缓存好编译好的公式，因为编译有损耗
    }

    override fun onDisable() {
        EnchantLoader.unregister()
    }
}