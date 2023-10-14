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

import com.mojang.brigadier.StringReader
import me.arasple.mc.trchat.taboolib.common.reflect.Reflex.Companion.invokeMethod
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.ItemTag
import net.md_5.bungee.api.chat.hover.content.Item
import net.md_5.bungee.chat.ComponentSerializer
import net.minecraft.nbt.MojangsonParser
import net.minecraft.nbt.NBTTagCompound
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.module.nms.NMSItem
import java.lang.reflect.Constructor
import java.util.concurrent.ConcurrentLinkedQueue

object ComponentUtils {

    val nmsItemConstructor: Constructor<*>;

    init {
        println(
            NMSItem.asNMSCopy(ItemStack(Material.STONE))
                .javaClass
                .constructors
                .map { it.parameterTypes.map { it.name } }
        )
        nmsItemConstructor =
            NMSItem.asNMSCopy(ItemStack(Material.STONE))
                .javaClass
                .constructors
                .find {
                    it.parameterTypes.size == 1 &&
                            it.parameters.getOrNull(0)?.type?.name == "NBTTagCompound"
                }!!
    }

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
                val item = hover.contents[0] as Item
                val id = item.id
                val cnt = item.count
                val tag = item.tag

                val nbt: NBTTagCompound? = MojangsonParser(StringReader(tag.nbt)).invokeMethod("a")
                println(nbt)
                //success!
                val nmsItem: Any = nmsItemConstructor.newInstance(nbt)
                println("BKITEM: " + NMSItem.asBukkitCopy(nmsItem))
                //未通过测试

                hover.contents[0] = Item(id, cnt, ItemTag.ofNbt("{}"))
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