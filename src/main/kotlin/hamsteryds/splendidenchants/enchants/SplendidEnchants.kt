package hamsteryds.splendidenchants.enchants

import taboolib.common.platform.Plugin
import taboolib.common.platform.function.info
import taboolib.common.platform.function.releaseResourceFile

object SplendidEnchants : Plugin() {

    override fun onEnable() {
        releaseResourceFile("nmsl.yml")
        EnchantmentLoader.initialize()
        info("Successfully running ExamplePlugin!")
    }
}