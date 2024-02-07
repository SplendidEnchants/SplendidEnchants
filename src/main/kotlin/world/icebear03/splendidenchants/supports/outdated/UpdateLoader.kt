package world.icebear03.splendidenchants.supports.outdated

import taboolib.common.io.runningResources
import taboolib.common.io.runningResourcesInJar
import taboolib.common.platform.function.releaseResourceFile

object UpdateLoader {

    fun load() {
        releaseUpdateFiles()
    }

    fun releaseUpdateFiles() {
        runningResourcesInJar.keys.filter { it.startsWith("updates/") }.forEach {
            releaseResourceFile(it, false)
        }
    }
}