package world.icebear03.splendidenchants.enchant.mechanism.entry.`object`

import com.mcstarrysky.starrysky.i18n.sendLang
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import taboolib.module.nms.getI18nName
import world.icebear03.splendidenchants.api.toLoc
import world.icebear03.splendidenchants.enchant.mechanism.entry.internal.ObjectEntry
import world.icebear03.splendidenchants.enchant.mechanism.entry.internal.objString
import java.util.*

object ObjectEntity : ObjectEntry<Entity>() {

    override fun modify(
        obj: Entity,
        cmd: String,
        params: List<String>
    ): Boolean {
        when (cmd) {
            "传送" -> obj.teleport(params[0].toLoc())
            "发送信息" -> {
                val tmp = if (params.size > 1) {
                    buildList<Pair<String, Any>> {
                        for (i in 1 until params.size step 2)
                            this += params[i] to params[i + 1]
                    }.toTypedArray()
                } else arrayOf()
                obj.sendLang(params[0], *tmp)
            }
        }
        return true
    }

    override fun get(from: Entity, objName: String): Pair<ObjectEntry<*>, Any?> {
        return when (objName) {
            "下落高度" -> objString.h(from.fallDistance)
            "名称" -> objString.h(from.customName ?: from.getI18nName())
            else -> objString.h(null)
        }
    }

    override fun holderize(obj: Entity) = this to "实体=${obj.uniqueId}"

    override fun disholderize(holder: String) = Bukkit.getEntity(UUID.fromString(holder.replace("实体=", "")))
}