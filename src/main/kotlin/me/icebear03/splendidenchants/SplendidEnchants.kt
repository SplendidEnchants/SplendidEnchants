package me.icebear03.splendidenchants

import taboolib.common.platform.Plugin
import taboolib.common.platform.function.info
import taboolib.common.platform.function.releaseResourceFile

object SplendidEnchants : Plugin() {
    override fun onEnable() {
        releaseResourceFile("test.yml")
        info("Successfully running ExamplePlugin!")
    }
}