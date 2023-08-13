package world.icebear03.splendidenchants.api.internal

import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.releaseResourceFile
import taboolib.module.configuration.Configuration
import java.io.File


object YamlUpdater {

    //最前面无需带/或\
    fun loadAndUpdate(path: String, forceUpdate: List<String> = emptyList()): Configuration {
        val fileName = path.split("/").last()
        val directoryName = getDataFolder().absolutePath + "/" + path.replace(fileName, "")
        //希望不要锁定的是一个同名文件夹
        val file = File(directoryName, fileName)
//        println(file)
        if (!file.exists()) {
            releaseResourceFile(path, true)
            return Configuration.loadFromFile(file)
        }
        val old = Configuration.loadFromFile(file)
        releaseResourceFile(path, true)
        val new = Configuration.loadFromFile(file)

        old.getKeys(true).forEach {
            if (!old.isConfigurationSection(it) && new.contains(it)) {
                var flag = true
                forceUpdate.forEach { forced ->
                    if (it.startsWith(forced))
                        flag = false
                }
                if (flag /*&& isTypeConsistent(new[it], old[it])*/) //FIXME 如何检查新旧配置同路径对应的value同类型，比如 String对String， 数字对数字
                    new[it] = old[it]
            }
        }
        new.saveToFile(file)

        return Configuration.loadFromFile(file)
    }
}

fun Configuration.Companion.loadAndUpdate(path: String, forceUpdate: List<String> = emptyList()): Configuration {
    return YamlUpdater.loadAndUpdate(path, forceUpdate)
}