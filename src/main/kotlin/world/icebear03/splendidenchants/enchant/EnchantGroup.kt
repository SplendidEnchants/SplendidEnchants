package world.icebear03.splendidenchants.enchant

import org.bukkit.enchantments.Enchantment
import taboolib.common.platform.function.console
import taboolib.module.configuration.Configuration
import world.icebear03.splendidenchants.api.enchant
import world.icebear03.splendidenchants.api.enchants
import world.icebear03.splendidenchants.enchant.data.rarity
import world.icebear03.splendidenchants.util.loadAndUpdate


val groups = mutableMapOf<String, EnchantGroup>()

data class EnchantGroup(
    val name: String,
    val enchants: List<SplendidEnchant>,
    val skull: String?,
    val maxCoexist: Int
) {

    companion object {

        fun load() {
            groups.clear()

            Configuration.loadAndUpdate("enchants/group.yml").run {
                getKeys(false).forEach { name ->
                    val enchants = getStringList("$name.enchants").mapNotNull { enchant(it) }.toMutableList()
                    getStringList("$name.rarities").forEach { enchants += enchants(rarity(it)) }

                    groups[name] = EnchantGroup(
                        name,
                        enchants,
                        getString("$name.skull"),
                        getInt("$name.max_coexist", 1),
                    )
                }
            }

            console().sendMessage("    Successfully load §6${groups.size} groups")
        }
    }
}

fun enchantGroup(name: String?): EnchantGroup? = groups[name]

fun Enchantment.isIn(name: String): Boolean = isIn(enchantGroup(name))

fun Enchantment.isIn(group: EnchantGroup?): Boolean = group?.enchants?.find { it.key == key } != null