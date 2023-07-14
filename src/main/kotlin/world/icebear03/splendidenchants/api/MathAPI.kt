package world.icebear03.splendidenchants.api

object MathAPI {

    fun numToRoman(num: Int, ignoreI: Boolean, hasPreviousBlank: Boolean = false): String {
        if (num == 1 && ignoreI)
            return ""
        var number = num
        var rNumber = StringBuilder()
        val aArray = intArrayOf(1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1)
        val rArray = arrayOf(
            "M", "CM", "D", "CD", "C", "XC", "L", "XL", "X",
            "IX", "V", "IV", "I"
        )
        if (number < 1 || number > 3999) {
            rNumber = StringBuilder("-1")
        } else {
            for (i in aArray.indices) {
                while (number >= aArray[i]) {
                    rNumber.append(rArray[i])
                    number -= aArray[i]
                }
            }
        }
        return if (hasPreviousBlank) " $rNumber" else rNumber.toString()
    }
}