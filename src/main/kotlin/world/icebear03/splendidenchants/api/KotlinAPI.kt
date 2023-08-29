package world.icebear03.splendidenchants.api

import taboolib.common5.cint

@Suppress("UNUSED_PARAMETER")
operator fun <T> List<T>.get(index: Int, fuckKotlin: Int) = getOrNull(index)

fun <T> List<T>.subList(index: Int) = if (size > index) subList(index, size) else emptyList()

operator fun <T> List<T>.get(index: Int, default: T) = getOrElse(index) { default }

val number = "-?[1-9]\\d*".toRegex()

val String.numbers get() = number.findAll(this).toList().map { it.value.cint }