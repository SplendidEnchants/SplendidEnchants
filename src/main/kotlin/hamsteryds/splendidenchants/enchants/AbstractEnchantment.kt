package hamsteryds.splendidenchants.enchants

import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import java.io.File

/**
 * SplendidEnchants
 * hamsteryds.splendidenchants.enchants.AbstractEnchantment
 *
 * @author mical
 * @since 2023/6/19 10:25 AM
 */
abstract class AbstractEnchantment(p0: NamespacedKey, val file: File) : Enchantment(p0)
