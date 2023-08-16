package world.icebear03.splendidenchants.api

@Suppress("UNUSED_PARAMETER")
operator fun <T> List<T>.get(index: Int, fuckKotlin: Int) = getOrNull(index)

fun <T> List<T>.subList(index: Int) = subList(index, size)