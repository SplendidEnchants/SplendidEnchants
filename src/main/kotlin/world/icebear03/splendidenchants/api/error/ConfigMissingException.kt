package world.icebear03.splendidenchants.api.error

import taboolib.common.platform.function.severe
import java.io.File

class ConfigMissingException(val file: File, val path: String) : Exception() {
    override fun printStackTrace() {
        severe("${file.name}缺失${path}设置")
    }
}

fun missingConfig(file: File, path: String): Nothing {
    throw ConfigMissingException(file, path)
}