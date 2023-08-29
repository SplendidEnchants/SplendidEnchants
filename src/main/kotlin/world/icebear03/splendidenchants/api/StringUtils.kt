package world.icebear03.splendidenchants.api

import com.mcstarrysky.starrysky.i18n.asLangText
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.function.console
import taboolib.common5.cbool
import taboolib.common5.cdouble
import taboolib.module.kether.compileToJexl
import kotlin.math.roundToInt

fun String.replace(holders: List<Pair<String, Any>>): String {
    var tmp = this
    holders.forEach { (holder, value) -> tmp = tmp.replace("{$holder}", "$value") }
    return tmp
}

fun String.replace(holders: Map<String, Any>): String = replace(holders.toList())

fun String.replace(vararg holders: Pair<String, Any>): String = replace(holders.toList())

val stringCalcSymbols = listOf("==", "~>", "~<", "~~", "``")

fun String.calculate(holders: List<Pair<String, Any>>): String {
    return replace(holders).run {
        try {
            compileToJexl().eval().toString()
        } catch (ignored: Exception) {
            var secondHand = this
            var flag = false
            split("&&").map { it.split("||") }.flatten().forEach { origin ->
                stringCalcSymbols.forEach { symbol ->
                    if (origin.contains(symbol)) {
                        flag = true
                        val a = this.split(symbol)[0]
                        val b = this.split(symbol)[1]
                        val result = when (symbol) {
                            "==" -> a == b
                            "~>" -> a.contains(b)
                            "~<" -> b.contains(a)
                            "+>" -> a.startsWith(b)
                            "->" -> a.endsWith(b)
                            "+<" -> b.startsWith(a)
                            "-<" -> b.endsWith(a)
                            else -> false
                        }
                        secondHand = secondHand.replace(origin, result.toString())
                    }
                }
            }
            if (flag) secondHand.calculate()
            else this
        }
    }
}

fun String.calculate(holders: Map<String, Any>): String = calculate(holders.toList())
fun String.calcToDouble(holders: Map<String, Any>): Double = calculate(holders).cdouble
fun String.calcToBoolean(holders: Map<String, Any>): Boolean = calculate(holders).cbool
fun String.calcToInt(holders: Map<String, Any>): Int = calcToDouble(holders).roundToInt()
fun String.calculate(vararg holders: Pair<String, Any>): String = calculate(holders.toList())
fun String.calcToDouble(vararg holders: Pair<String, Any>): Double = calculate(*holders).cdouble
fun String.calcToBoolean(vararg holders: Pair<String, Any>): Boolean = calculate(*holders).cbool
fun String.calcToInt(vararg holders: Pair<String, Any>): Int = calcToDouble(*holders).roundToInt()

fun String.translate(who: ProxyCommandSender = console(), prefix: Boolean = true) = who.asLangText(this, prefix = prefix).toPlainText()
