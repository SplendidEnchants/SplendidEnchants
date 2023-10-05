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
import net.md_5.bungee.chat.ComponentSerializer

object ComponentUtils {

    /**
     * 处理聊天展示
     */
    fun applyItemDisplay(component: BaseComponent): BaseComponent {
        TODO("Not yet implemented")
    }

    /**
     * 将 BaseComponent 转为原始信息
     */
    fun parseRaw(component: BaseComponent): String {
        return ComponentSerializer.toString(component)
    }
}