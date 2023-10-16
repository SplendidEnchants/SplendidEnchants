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
package world.icebear03.splendidenchants.api.internal

import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.ItemTag
import net.md_5.bungee.api.chat.hover.content.Item
import net.md_5.bungee.chat.ComponentSerializer
import org.bukkit.entity.Player
import taboolib.module.nms.nmsProxy
import world.icebear03.splendidenchants.api.internal.nms.NMS
import world.icebear03.splendidenchants.enchant.EnchantDisplayer
import java.util.concurrent.ConcurrentLinkedQueue

object ComponentUtils {

    /**
     * 处理聊天展示
     */
    fun applyItemDisplay(component: BaseComponent, player: Player): BaseComponent {
        val queue = ConcurrentLinkedQueue<BaseComponent>()
        queue.add(component)
        var processing: BaseComponent
        while (queue.isNotEmpty()) {
            processing = queue.poll()
            processing.extra?.let { queue.addAll(it) }

            val hover = processing.hoverEvent ?: continue
            if (hover.action == HoverEvent.Action.SHOW_ITEM) {
                val content = hover.contents[0] as Item
                val id = content.id
                val cnt = content.count
                println("未修改物品的nbt: " + nmsProxy<NMS>().itemToJson(content))
                println("未修改物品的bukkit物品堆: " + nmsProxy<NMS>().jsonToItem(nmsProxy<NMS>().itemToJson(content)))
                val item = nmsProxy<NMS>().jsonToItem(nmsProxy<NMS>().itemToJson(content)) // 这是未修改过的物品
                val newItem = EnchantDisplayer.display(item, player) // 生成展示过的物品
                println("新物品nbt: " + nmsProxy<NMS>().bkItemToJson(newItem))
                val newNBT = nmsProxy<NMS>().bkItemToJson(newItem) // 新物品 NBT

                hover.contents[0] = Item(id, cnt, ItemTag.ofNbt(newNBT)) // 设置回去
                processing.hoverEvent = hover
            }
        }
        return component
    }

    /**
     * 将 BaseComponent 转为原始信息
     */
    fun parseRaw(component: BaseComponent): String {
        return ComponentSerializer.toString(component)
    }
}