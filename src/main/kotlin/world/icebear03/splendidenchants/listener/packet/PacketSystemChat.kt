/*
 *  Copyright (C) <2023>  <Mical>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package world.icebear03.splendidenchants.listener.packet

import org.bukkit.entity.Player
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.chat.ComponentText
import taboolib.module.chat.Components
import taboolib.module.nms.PacketSendEvent
import world.icebear03.splendidenchants.api.internal.AdventureUtils
import world.icebear03.splendidenchants.api.internal.ComponentUtils
import world.icebear03.splendidenchants.api.internal.isPaper

object PacketSystemChat {

    @SubscribeEvent(priority = EventPriority.MONITOR)
    fun e(e: PacketSendEvent) {
        if (e.packet.name == "ClientboundSystemChatPacket") {
            val player = e.player
            if (isPaper) {
                val adventure = e.packet.read<Any>("adventure\$content") ?: return
                val taboo = AdventureUtils.toTabooLibComponent(adventure) as ComponentText
                val result = AdventureUtils.fromTabooLibComponent(modify(taboo, player))
                e.packet.write("adventure\$content", result)
            } else {
                val taboo = Components.parseRaw(e.packet.read<String>("content") ?: return)
                e.packet.write("content", modify(taboo, player).toRawMessage())
            }
        }
    }

    private fun modify(taboo: ComponentText, player: Player): ComponentText {
        return Components.parseRaw(ComponentUtils.parseRaw(ComponentUtils.applyItemDisplay(taboo.toSpigotObject(), player)))
    }
}