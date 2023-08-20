package world.icebear03.splendidenchants.enchant.mechanism.entry.`object`

import com.mcstarrysky.starrysky.i18n.sendLang
import org.bukkit.entity.Entity
import world.icebear03.splendidenchants.api.calcToDouble
import world.icebear03.splendidenchants.api.loc
import world.icebear03.splendidenchants.api.replace
import world.icebear03.splendidenchants.api.subList

object ObjectEntity {

    fun modify(
        entity: Entity,
        params: List<String>,
        holders: MutableMap<String, Any>
    ): Boolean {
        
        holders["下落高度"] = entity.fallDistance

        val variabled = params.map { it.replace(holders) }
        val type = variabled[0]
        val after = variabled.subList(1)

        when (type) {
            "传送" -> entity.teleport(
                loc(
                    after[0],
                    after[1].calcToDouble(),
                    after[2].calcToDouble(),
                    after[3].calcToDouble()
                )
            )

            "发送信息" -> {
                val tmp = if (after.size > 1) {
                    buildList<Pair<String, Any>> {
                        for (i in 1 until after.size step 2)
                            this += after[i] to after[i + 1]
                    }.toTypedArray()
                } else arrayOf()
                entity.sendLang(after[0], *tmp)
            }

            else -> return false
        }
        return true
    }
}