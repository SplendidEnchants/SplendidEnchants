package world.icebear03.splendidenchants.api

import taboolib.module.kether.compileToJexl
import kotlin.math.roundToInt

fun String.replace(holders: List<Pair<String, Any>>): String {
    var tmp = this
    holders.forEach { (holder, value) -> tmp = tmp.replace("{$holder}", "$value") }
    return tmp
}

fun String.replace(holders: Map<String, Any>): String = replace(holders.toList())

fun String.replace(vararg holders: Pair<String, Any>): String = replace(holders.toList())

fun String.calculate(holders: List<Pair<String, Any>>): String {
    return replace(holders).run {
        try {
            compileToJexl().eval().toString()
        } catch (ignored: Exception) {
            this
        }
    }
}

fun String.calculate(holders: Map<String, Any>): String = calculate(holders.toList())
fun String.calculate(vararg holders: Pair<String, Any>): String = calculate(holders.toList())
fun String.calcToDouble(vararg holders: Pair<String, Any>): Double = calculate(*holders).toDouble()
fun String.calcToBoolean(vararg holders: Pair<String, Any>): Boolean = calculate(*holders).toBoolean()
fun String.calcToInt(vararg holders: Pair<String, Any>): Int = calcToDouble(*holders).roundToInt()