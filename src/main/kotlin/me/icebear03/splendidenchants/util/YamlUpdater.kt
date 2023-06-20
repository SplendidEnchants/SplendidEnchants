package me.icebear03.splendidenchants.util

import taboolib.common.platform.function.releaseResourceFile
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import java.nio.charset.StandardCharsets

/**
 * SplendidEnchants
 * me.icebear03.splendidenchants.util.YamlUpdater
 *
 * @author mical
 * @since 2023/6/20 9:38 PM
 */
object YamlUpdater {

    fun loadAndUpdate(path: String, whitelist: List<String>): Configuration {
        val resource = YamlUpdater::class.java.getResourceAsStream(path) ?: return Configuration.empty()
        val source = resource.readBytes().toString(StandardCharsets.UTF_8)
        val sourceConfig = Configuration.loadFromString(source, Type.YAML)
        val file = releaseResourceFile(path)
        val config = Configuration.loadFromFile(file)
        sourceConfig.getKeys(false).forEach {
            if (it in whitelist && config[it] != sourceConfig[it]) {
                config[it] = sourceConfig[it]
            }
        }
        config.saveToFile(file)
        return config
    }
}

fun Configuration.Companion.loadAndUpdate(path: String, whitelist: List<String>): Configuration {
    return YamlUpdater.loadAndUpdate(path, whitelist)
}