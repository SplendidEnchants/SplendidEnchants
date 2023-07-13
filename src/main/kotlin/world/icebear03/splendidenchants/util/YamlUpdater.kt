package world.icebear03.splendidenchants.util

import taboolib.common.platform.function.releaseResourceFile
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import java.nio.charset.StandardCharsets

/**
 * SplendidEnchants
 * world.icebear03.splendidenchants.util.internal.YamlUpdater
 *
 * @author mical
 * @since 2023/6/20 9:38 PM
 */
object YamlUpdater {

    fun loadAndUpdate(path: String, whitelist: List<String> = emptyList()): Configuration {
        val resource = YamlUpdater::class.java.classLoader.getResourceAsStream(path) ?: return Configuration.empty()
        val source = resource.readBytes().toString(StandardCharsets.UTF_8)
        val sourceConfig = Configuration.loadFromString(source, Type.YAML)
        val file = releaseResourceFile(path)
        val config = Configuration.loadFromFile(file)
        if (whitelist.isEmpty() && config.saveToString() != sourceConfig.saveToString()) {
            sourceConfig.saveToFile(file)
            return Configuration.loadFromFile(file)
        }
        var pass = true
        for (key in whitelist) {
            if (!config.contains(key) && sourceConfig.contains(key)) {
                config[key] = sourceConfig[key]
                pass = false
            } else {
                val obj = sourceConfig[key]
                if (obj is ConfigurationSection) {
                    if (config[key] !is ConfigurationSection) {
                        config[key] = obj
                        pass = false
                    } else {
                        val mapOld = getValues(config[key] as ConfigurationSection)
                        if (mapOld != getValues(obj)) {
                            config[key] = obj
                            pass = false
                        }
                    }
                } else {
                    if (config[key] != obj) {
                        config[key] = obj
                        pass = false
                    }
                }
            }
        }
        if (!pass) {
            config.saveToFile(file)
        }
        return config
    }

    private fun getValues(section: ConfigurationSection): Map<String, Any?> {
        val result = hashMapOf<String, Any?>()
        section.getKeys(false).forEach { key ->
            val obj = section[key]
            if (obj is ConfigurationSection) {
                result += getValues(obj)
            } else {
                result += key to obj
            }
        }
        return result
    }
}

fun Configuration.Companion.loadAndUpdate(path: String, whitelist: List<String> = emptyList()): Configuration {
    return YamlUpdater.loadAndUpdate(path, whitelist)
}