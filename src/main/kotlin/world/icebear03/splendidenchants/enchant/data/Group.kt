package world.icebear03.splendidenchants.enchant.data

import org.bukkit.enchantments.Enchantment
import taboolib.common.platform.function.console
import taboolib.module.configuration.Configuration
import world.icebear03.splendidenchants.api.splendidEt
import world.icebear03.splendidenchants.api.splendidEts
import world.icebear03.splendidenchants.enchant.SplendidEnchant
import world.icebear03.splendidenchants.util.loadAndUpdate


val groups = mutableMapOf<String, Group>()

data class Group(
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
                    val enchants = getStringList("$name.enchants").mapNotNull { splendidEt(it) }.toMutableList()
                    getStringList("$name.rarities").forEach { enchants += splendidEts(rarity(it)) }

                    groups[name] = Group(
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

fun group(name: String?): Group? = groups[name]

fun Enchantment.isIn(name: String): Boolean = isIn(group(name))

fun Enchantment.isIn(group: Group?): Boolean = group?.enchants?.find { it.key == key } != null