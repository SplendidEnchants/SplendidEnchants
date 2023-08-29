package world.icebear03.splendidenchants.supports

import com.loohp.interactivechat.api.events.ItemPlaceholderEvent
import taboolib.common.platform.function.registerBukkitListener
import world.icebear03.splendidenchants.enchant.EnchantDisplayer

object HookInteractiveChat {

    fun load() {
        registerBukkitListener(ItemPlaceholderEvent::class.java) { event ->
            event.itemStack = EnchantDisplayer.display(event.itemStack, event.receiver)
        }
    }
}