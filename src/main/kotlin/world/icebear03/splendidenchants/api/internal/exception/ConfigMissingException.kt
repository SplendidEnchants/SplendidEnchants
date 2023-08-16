package world.icebear03.splendidenchants.api.internal.exception

import java.io.File

class ConfigMissingException(file: File, path: String) : EnchantException("配置文件 ${file.name} 缺失配置项: $path")

@Suppress("NOTHING_TO_INLINE")
inline fun missingConfig(file: File, path: String): Nothing {
    throw ConfigMissingException(file, path)
}