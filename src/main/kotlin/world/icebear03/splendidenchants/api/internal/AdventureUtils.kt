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

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import taboolib.common.util.unsafeLazy
import taboolib.module.chat.Components
import taboolib.module.chat.Source

object AdventureUtils {

    private val gson: GsonComponentSerializer by unsafeLazy {
        GsonComponentSerializer.gson()
    }

    fun toTabooLibComponent(component: Any): Source {
        component as Component
        return Components.parseRaw(gson.serialize(component))
    }

    fun fromTabooLibComponent(component: Source): Any {
        return gson.deserialize(component.toRawMessage())
    }
}