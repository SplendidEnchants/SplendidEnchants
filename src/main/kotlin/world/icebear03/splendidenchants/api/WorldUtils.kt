package world.icebear03.splendidenchants.api

import org.bukkit.Bukkit
import org.bukkit.Location

val Location.serialized get() = "${world.name},$blockX,$blockY,$blockZ"

fun String.toLoc(): Location {
    val split = split(",")
    return Location(Bukkit.getWorld(split[0]), split[1].toDouble(), split[2].toDouble(), split[3].toDouble())
}