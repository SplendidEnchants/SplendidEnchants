package me.icebear03.splendidenchants

import me.icebear03.splendidenchants.enchant.EnchantGroup
import me.icebear03.splendidenchants.enchant.EnchantLoader
import me.icebear03.splendidenchants.enchant.data.Rarity
import me.icebear03.splendidenchants.enchant.data.Target
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.info

object SplendidEnchants : Plugin() {
    var plugin: SplendidEnchants? = null

    override fun onEnable() {
        plugin = this
        info("Loading SplendidEnchants...")

        Config.initialize()

        Rarity.initialize()
        Target.initialize()
        EnchantLoader.initialize()
        EnchantGroup.initialize()
        //    println("3+7-a*9.5".compileToJexl().eval(mapOf("a" to 6)))
        // 注意需提前缓存好，建议在加载附魔的时候就缓存好编译好的公式，因为编译有损耗
    }

    override fun onDisable() {
        EnchantLoader.unregister()
    }
}