package hamsteryds.splendidenchants.enchants

import org.bukkit.plugin.java.JavaPlugin

open class SplendidEnchants : JavaPlugin() {
    override fun onEnable() {
        logger.info("SplendidEnchants in Kotlin loaded!")
    }
}