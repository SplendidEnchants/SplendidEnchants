package world.icebear03.splendidenchants.api

import org.bukkit.FluidCollisionMode
import org.bukkit.entity.Player

fun Player.blockLookingAt(
    range: Double = 50.0,
    fluidCollisionMode: FluidCollisionMode = FluidCollisionMode.NEVER
) = rayTraceBlocks(range, fluidCollisionMode)?.hitBlock