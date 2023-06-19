package hamsteryds.splendidenchants.enchants

import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.enchantments.EnchantmentTarget
import org.bukkit.inventory.ItemStack
import taboolib.common.io.newFile
import taboolib.common.platform.function.getDataFolder
import taboolib.module.configuration.Configuration

/**
 * SplendidEnchants
 * hamsteryds.splendidenchants.enchants.TestEnchantment
 *
 * @author mical
 * @since 2023/6/19 10:26 AM
 */
class TestEnchantment : AbstractEnchantment(NamespacedKey("nmsl", "nmsl"), newFile(getDataFolder(), "nmsl.yml")) {

    private val config = Configuration.loadFromFile(file)

    @Deprecated("Deprecated in Java")
    override fun getName(): String = config.getString("name") ?: error("no name")


    override fun getMaxLevel(): Int = 38

    override fun getStartLevel(): Int = 1

    override fun getItemTarget(): EnchantmentTarget = EnchantmentTarget.TOOL

    override fun isTreasure(): Boolean = false

    //诅咒
    @Deprecated("Deprecated in Java")
    override fun isCursed(): Boolean = false

    override fun conflictsWith(p0: Enchantment): Boolean = false

    override fun canEnchantItem(p0: ItemStack): Boolean = true
}