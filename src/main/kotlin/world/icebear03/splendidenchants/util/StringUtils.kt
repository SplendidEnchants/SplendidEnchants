package world.icebear03.splendidenchants.util

import taboolib.module.kether.compileToJexl

fun String.replace(holders: List<Pair<String, String>>): String {
    var tmp = this
    holders.forEach { (holder, value) -> tmp = tmp.replace("{$holder}", value) }
    return tmp
}

fun String.replace(holders: Map<String, String>): String = replace(holders.toList())

fun String.replace(vararg holders: Pair<String, String>): String = replace(holders.toList())

fun String.calculate(holders: List<Pair<String, String>>): String = replace(holders).compileToJexl().eval().toString()
fun String.calculate(holders: Map<String, String>): String = calculate(holders.toList())
fun String.calculate(vararg holders: Pair<String, String>): String = calculate(holders.toList())