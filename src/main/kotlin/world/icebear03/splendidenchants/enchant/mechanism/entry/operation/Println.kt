package world.icebear03.splendidenchants.enchant.mechanism.entry.operation

import com.mcstarrysky.starrysky.i18n.sendLang
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import world.icebear03.splendidenchants.api.translate

object Println {
    fun println(entity: LivingEntity, text: String) {
        println(text.translate())
        if (entity is Player)
            entity.sendLang(text)
    }
}