package world.icebear03.splendidenchants.supports

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.reflect.StructureModifier
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket
import org.bukkit.Bukkit
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.info
import taboolib.common.platform.function.submit
import world.icebear03.splendidenchants.SplendidEnchants.plugin
import world.icebear03.splendidenchants.supports.HookProtocolLib.modifierChatComponent
import java.util.function.UnaryOperator


object HookProtocolLib {

    fun load() {
        ProtocolLibrary.getProtocolManager().addPacketListener(
            object : PacketAdapter(
                plugin,
                PacketType.Play.Server.SYSTEM_CHAT
            ) {
                override fun onPacketSending(event: PacketEvent) {
                    val packet = event.packet

                    info(packet.getChatComponent())

                    packet.modifierChatComponent(Component.text("FUCK YOU"))

//                    packet.chatComponents.write(0, ComponentConverter.fromBaseComponent(baseComponent))
                }
            }
        )
    }

    fun PacketContainer.getChatComponent(): Component? {
        val adventureModifier: StructureModifier<Component>? = getSpecificModifier(Component::class.java)
        return adventureModifier?.readSafely(0)
            ?: strings.readSafely(0)?.let { GsonComponentSerializer.gson().deserialize(it) }
    }

    fun PacketContainer.modifierChatComponent(component: Component) {
        val adventureModifier: StructureModifier<Component> = getSpecificModifier(Component::class.java)
        adventureModifier.modify(0) { component }
    }
}