package world.icebear03.splendidenchants.enchant.mechanism.entry.`object`

import com.mcstarrysky.starrysky.i18n.sendLang
import org.bukkit.entity.Entity
import world.icebear03.splendidenchants.api.calcToDouble
import world.icebear03.splendidenchants.api.loc
import world.icebear03.splendidenchants.api.replace

object ObjectEntity {

    fun modify(
        entity: Entity,
        params: List<String>,
        holders: MutableMap<String, Any>
    ): Boolean {

        val variabled = params.map { it.replace(holders) }

        when (variabled[0]) {
            "传送" -> entity.teleport(
                loc(
                    variabled[1],
                    variabled[2].calcToDouble(),
                    variabled[3].calcToDouble(),
                    variabled[4].calcToDouble()
                )
            )

            "发送信息" -> {
                val tmp = if (variabled.size > 2) {
                    buildList<Pair<String, Any>> {
                        for (i in 2 until variabled.size step 2)
                            this += variabled[i] to variabled[i + 1]
                    }.toTypedArray()
                } else arrayOf()
                entity.sendLang(variabled[1], *tmp)
            }

            else -> return false
        }
        return true
    }
}