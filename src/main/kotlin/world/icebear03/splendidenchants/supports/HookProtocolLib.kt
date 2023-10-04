package world.icebear03.splendidenchants.supports

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.ComponentConverter
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.ItemTag
import net.md_5.bungee.api.chat.TranslatableComponent
import net.md_5.bungee.api.chat.hover.content.Item
import org.bukkit.Bukkit

object HookProtocolLib {

    fun load() {
        ProtocolLibrary.getProtocolManager().addPacketListener(
            object : PacketAdapter(
                Bukkit.getPluginManager().getPlugin("SplendidEnchants")!!,
//                ListenerPriority.MONITOR,
                PacketType.Play.Server.PLAYER_COMBAT_KILL
            ) {
                override fun onPacketSending(event: PacketEvent) {
                    val packet = event.packet
                    val wrappedComponent = packet.chatComponents.read(0)!!
                    val baseComponent = ComponentConverter.fromWrapper(wrappedComponent)[0]

                    println("前:$baseComponent")

                    fun adapt(component: BaseComponent) {
                        component.hoverEvent?.let { hoverEvent ->
                            if (hoverEvent.action == HoverEvent.Action.SHOW_ITEM) {
                                val itemContent = hoverEvent.contents.filterIsInstance<Item>()[0]
                                val id = itemContent.id
                                val nbt = itemContent.tag.nbt

                                val adapted = nbt

                                hoverEvent.contents[0] = Item("stone", -1, ItemTag.ofNbt("{}"))
                                component.hoverEvent = hoverEvent
                            }
                        }
                        component.extra?.forEach { bc -> adapt(bc) }
                        if (component is TranslatableComponent)
                            component.with?.forEach { bc -> adapt(bc) }
                    }

                    adapt(baseComponent)
                    println("后:$baseComponent")
                    packet.chatComponents.write(0, ComponentConverter.fromBaseComponent(baseComponent))
                }
            }
        )
    }
}