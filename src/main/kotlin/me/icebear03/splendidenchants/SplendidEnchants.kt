package me.icebear03.splendidenchants

import taboolib.common.platform.Plugin
import taboolib.common.platform.function.info
import taboolib.common.platform.function.releaseResourceFile

/**
 * SplendidEnchants
 * me.icebear03.splendidenchants.SplendidEnchants
 *
 * @author mical
 * @since 2023/6/19 7:46 PM
 */
object SplendidEnchants : Plugin() {

    override fun onEnable() {
        info("Loading SplendidEnchants...")
        releaseResourceFile("test.yml")
    }
}