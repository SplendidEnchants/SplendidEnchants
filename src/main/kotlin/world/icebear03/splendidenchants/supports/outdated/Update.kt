package world.icebear03.splendidenchants.supports.outdated

import taboolib.common5.cint
import taboolib.module.configuration.Configuration

data class Update(
    val code: String,
    val config: Configuration
) {
    val version: String
    val tag: Int
    val type: VersionType
//    val data: String
//    val contents: List<String>
//    val checked: Boolean

    val manual = mutableMapOf<String, Pair<String, String>>()

    init {
        version = code.split("-")[0]
        tag = code.split("-")[1].cint
//        type = VersionType.valueOf(code.split("-"))
    }
}
