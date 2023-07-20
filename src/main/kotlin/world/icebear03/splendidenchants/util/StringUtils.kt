package world.icebear03.splendidenchants.util

import taboolib.common.util.replaceWithOrder

object StringUtils {

    //label - leftReplacedParams
    fun replaceParams(
        params: List<String>,
        replacer: ArrayList<Pair<String, String>>,
        separator: String = ":"
    ): Pair<String, List<String>> {
        val combined = params.joinToString(separator).replaceWithOrder(*replacer.toArray()).split(separator)
        return combined[0] to combined.subList(1, combined.size)
    }
}