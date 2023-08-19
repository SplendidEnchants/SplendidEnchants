package world.icebear03.splendidenchants.api

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World

val Location.serialized get() = "${world.name},$blockX,$blockY,$blockZ"

fun String.toLoc(): Location {
    val split = split(",")
    return Location(Bukkit.getWorld(split[0]), split[1].toDouble(), split[2].toDouble(), split[3].toDouble())
}

fun Location.add(x: Number, y: Number, z: Number) {
    add(x.toDouble(), y.toDouble(), z.toDouble())
}

fun loc(worldName: String, x: Number, y: Number, z: Number): Location {
    return loc(Bukkit.getWorld(worldName)!!, x, y, z)
}

fun loc(world: World, x: Number, y: Number, z: Number): Location {
    return Location(world, x.toDouble(), y.toDouble(), z.toDouble())
}