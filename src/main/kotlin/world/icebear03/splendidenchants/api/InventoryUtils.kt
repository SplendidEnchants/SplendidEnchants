package world.icebear03.splendidenchants.api

import org.bukkit.entity.Player
import org.serverct.parrot.parrotx.ui.config.advance.ShapeConfiguration
import org.serverct.parrot.parrotx.ui.config.advance.TemplateConfiguration
import taboolib.module.ui.type.Chest
import taboolib.module.ui.type.PageableChest

fun Chest.setSlots(
    shape: ShapeConfiguration,
    templates: TemplateConfiguration,
    key: String,
    elements: List<Any?> = listOf(),
    vararg args: Pair<String, Any>
) {
    var tot = 0
    shape[key].forEach { slot ->
        val map = args.toMap().mapValues {
            val parts = it.value.toString().split("=")
            when (parts[0]) {
                "expression" -> parts[1].calcToInt("tot" to "$tot")
                "element" -> elements[parts[1, 0]?.calcToInt("tot" to "$tot") ?: tot, 0]
                else -> it.value
            }
        }
        set(slot, templates(key, slot, 0, false, "Fallback") { this += map })
        onClick(slot) { templates[it.rawSlot]?.handle(this, it) { this += map } }
        tot++
    }
}

fun Chest.load(
    shape: ShapeConfiguration,
    templates: TemplateConfiguration,
    player: Player,
    vararg ignored: String
) {
    val notAuto = ignored.toMutableList() + "Back"
    onBuild { _, inventory ->
        shape.all(*notAuto.toTypedArray()) { slot, index, item, _ ->
            inventory.setItem(slot, item(slot, index))
        }
    }

    if (shape["Back", true].isNotEmpty())
        setSlots(shape, templates, "Back", listOf(), "player" to player)

    onClick { event ->
        event.isCancelled = true
        if (event.rawSlot in shape && notAuto.none { shape[it, true].contains(event.rawSlot) }) {
            templates[event.rawSlot]?.handle(this, event, "player" to player)
        }
    }
}

fun <T> PageableChest<T>.pages(
    shape: ShapeConfiguration,
    templates: TemplateConfiguration
) {
    shape["Previous"].forEach { slot -> setPreviousPage(slot) { it, _ -> templates("Previous", slot, it) } }
    shape["Next"].forEach { slot -> setNextPage(slot) { it, _ -> templates("Next", slot, it) } }
}