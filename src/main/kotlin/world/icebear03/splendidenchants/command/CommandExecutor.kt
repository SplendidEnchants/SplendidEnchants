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
package world.icebear03.splendidenchants.command

import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.SimpleCommandBody
import world.icebear03.splendidenchants.api.i18n.asLangTextString

/**
 * SplendidEnchants
 * world.icebear03.splendidenchants.command.CommandExecutor
 *
 * @author mical
 * @since 2023/8/16 1:33 PM
 */
interface CommandExecutor {

    val command: SimpleCommandBody

    val name: String

    fun desc(sender: ProxyCommandSender): String {
        return sender.asLangTextString("command.subCommands.$name.description")
    }

    fun usage(sender: ProxyCommandSender): String {
        return sender.asLangTextString("command.subCommands.$name.usage")
    }
}