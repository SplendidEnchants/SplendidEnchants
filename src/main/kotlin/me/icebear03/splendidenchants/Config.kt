package me.icebear03.splendidenchants

import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.releaseResourceFile
import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigNode
import taboolib.module.configuration.Configuration
import java.io.File

/**
 * 主配置文件, 也就是 config.yml, 提供一些最基本最重要的配置.
 */
object Config {

    @Config
    lateinit var config: Configuration
        private set

    @ConfigNode("use_mini_message")
    var useMiniMessage: Boolean = false

    fun updateAndGetResource(path: String): Configuration {
        val file: File = File(getDataFolder(), path);
        if (!file.exists())
            releaseResourceFile(path)
        return Configuration.loadFromFile(file)
    }
}