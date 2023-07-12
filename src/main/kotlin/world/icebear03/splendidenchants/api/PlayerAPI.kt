package world.icebear03.splendidenchants.api

import org.bukkit.FluidCollisionMode
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import taboolib.platform.compat.replacePlaceholder

object PlayerAPI {

    fun convertPlaceHolders(string: String, player: Player): String {
        return string.replacePlaceholder(player)
    }

    fun lookingAtBlock(
        player: Player,
        range: Double = 50.0,
        fluidCollisionMode: FluidCollisionMode = FluidCollisionMode.NEVER
    ): Block? {
        val rayTrace = player.rayTraceBlocks(range, fluidCollisionMode) ?: return null
        return rayTrace.hitBlock
    }

    fun lookingAtEntity(player: Player): Entity? {
        return null
    }
}