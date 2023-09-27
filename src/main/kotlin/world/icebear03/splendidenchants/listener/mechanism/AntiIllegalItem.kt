package world.icebear03.splendidenchants.listener.mechanism

import com.mcstarrysky.starrysky.i18n.sendLang
import taboolib.common.platform.function.submit
import taboolib.common.platform.service.PlatformExecutor
import taboolib.module.nms.getI18nName
import taboolib.platform.util.giveItem
import taboolib.platform.util.onlinePlayers
import world.icebear03.splendidenchants.Config
import world.icebear03.splendidenchants.api.book
import world.icebear03.splendidenchants.api.display
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
                    var j = 0
                    while (j < enchants.size) {
                        val tmp = item.clone()
                        val et = enchants[j].first
                        tmp.removeEt(et)
                        val result = et.limitations.checkAvailable(checkList, tmp)
                        if (!result.first) {
                            enchants.removeAt(j)
                            player.giveItem(et.book(enchants[j].second))
                            item.removeEt(et)
                            player.sendLang(
                                "info.illegal-item",
                                "item" to item.getI18nName(),
                                "reason" to result.second,
                                "enchant" to et.display(null)
                            )
                        }
                        j++
                    }
                }
            }
        }
    }
}