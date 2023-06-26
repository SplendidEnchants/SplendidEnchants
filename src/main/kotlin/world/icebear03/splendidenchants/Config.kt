package world.icebear03.splendidenchants

import world.icebear03.splendidenchants.util.YamlUpdater
import taboolib.module.configuration.ConfigNode
import taboolib.module.configuration.Configuration

/**
 * 主配置文件, 也就是 config.yml, 提供一些最基本最重要的配置.
 */
object Config {

    lateinit var config: Configuration
        private set

    @ConfigNode("use_mini_message")
    var useMiniMessage: Boolean = false

    fun initialize() {
        config = YamlUpdater.loadAndUpdate("config.yml", arrayListOf())
    }
}