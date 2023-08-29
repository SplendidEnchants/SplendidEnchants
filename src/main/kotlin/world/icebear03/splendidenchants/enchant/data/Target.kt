package world.icebear03.splendidenchants.enchant.data

import org.bukkit.Material
import org.bukkit.inventory.EquipmentSlot
import taboolib.common.platform.function.console
import taboolib.module.configuration.Configuration
import world.icebear03.splendidenchants.api.internal.loadAndUpdate
import java.util.concurrent.ConcurrentHashMap

val targets = ConcurrentHashMap<String, Target>()

data class Target(
    val id: String,
    val name: String,
    val capability: Int,
    val activeSlots: List<EquipmentSlot>,
    val types: List<Material>,
    val skull: String?
) {

    companion object {
        fun load() {
            targets.clear()
            Configuration.loadAndUpdate("enchants/target.yml").run {
                getKeys(false).forEach { id ->
                    targets[id] = Target(
                        id,
                        getString("$id.name")!!,
                        getInt("$id.max"),
                        getStringList("$id.active_slots").map { EquipmentSlot.valueOf(it) },
                        getStringList("$id.types").map { Material.valueOf(it) },
                        getString("$id.skull")
                    )
                }
            }

            console().sendMessage("    Successfully load §6${targets.size} targets")
        }
    }
}

fun target(identifier: String?): Target? = targets[identifier] ?: targets.values.find { it.name == identifier }

fun Material.isIn(identifier: String?) = isIn(target(identifier))

fun Material.isIn(target: Target?): Boolean = target?.types?.contains(this) ?: false

val Material.belongedTargets get() = targets.values.filter(::isIn)

val Material.capability
    get() = belongedTargets.minOf {
        try {
            it.capability
        } catch (e: Exception) {
            println(it)
            println(it)
            32
        }
    }