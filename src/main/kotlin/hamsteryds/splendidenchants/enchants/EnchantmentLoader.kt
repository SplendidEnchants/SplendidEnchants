package hamsteryds.splendidenchants.enchants

import org.bukkit.enchantments.Enchantment
import taboolib.common.platform.function.info
import taboolib.common.platform.function.severe
import taboolib.library.reflex.Reflex.Companion.setProperty
import java.util.concurrent.ConcurrentHashMap

/**
 * SplendidEnchants
 * hamsteryds.splendidenchants.enchants.EnchantmentLoader
 *
 * @author mical
 * @since 2023/6/19 10:34 AM
 */
object EnchantmentLoader {

    val enchants = ConcurrentHashMap<String, Enchantment>()

    fun initialize() {
        try {
            Enchantment::class.java.setProperty("acceptingNew", value = true, isStatic = true)
            info(Enchantment.isAcceptingRegistrations())
            Enchantment.registerEnchantment(TestEnchantment().also { enchants[it.key.toString()] = it })
            println(Enchantment.values().map { it.key.toString() })
        } catch (ex: Throwable) {
            severe("注册测试附魔失败.")
            ex.printStackTrace()
        }
    }
}