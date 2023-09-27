package world.icebear03.splendidenchants.player

import taboolib.common.platform.function.submit
import taboolib.common.platform.service.PlatformExecutor
import taboolib.module.nms.getI18nName
import taboolib.platform.util.onlinePlayers
import taboolib.platform.util.sendLang
import world.icebear03.splendidenchants.Config
import world.icebear03.splendidenchants.api.fixedEnchants
import world.icebear03.splendidenchants.api.removeEt
import world.icebear03.splendidenchants.enchant.data.limitation.LimitType

object AntiIllegalItem {

    var enable = false
    var interval = 60L
    var checkList = listOf<LimitType>()
    var task: PlatformExecutor.PlatformTask? = null

    fun load() {
        enable = Config.config.getBoolean("anti_illegal_item.enable", false)
        interval = Config.config.getLong("anti_illegal_item.interval", 60L)
        checkList += Config.config.getStringList("anti_illegal_item.check_list").map {
            LimitType.valueOf(it)
        }

        if (!enable)
            return

        task?.cancel()
        task = submit(period = interval) {
            onlinePlayers.forEach { player ->
                val inv = player.inventory
                for (i in 0 until inv.size) {
                    val item = inv.getItem(i) ?: continue
                    val enchants = item.fixedEnchants.toList().toMutableList()
                    if (enchants.isEmpty()) continue
                    for (j in enchants.indices) {
                        val et = enchants[j].first
                        val result = et.limitations.checkAvailable(checkList, item)
                        if (!result.first) {
                            enchants.removeAt(j)
                            item.removeEt(et)
                            player.sendLang("info.illegal_item", "item", item.getI18nName(), "reason", result.second)
                        }
                    }
                }
            }
        }
    }
}