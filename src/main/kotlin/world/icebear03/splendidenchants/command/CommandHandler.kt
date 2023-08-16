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

import com.mcstarrysky.starrysky.command.CommandExecutor
import com.mcstarrysky.starrysky.command.CommandHandler
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import world.icebear03.splendidenchants.command.impl.*
import java.util.concurrent.ConcurrentHashMap

/**
 * SplendidEnchants
 * world.icebear03.splendidenchants.command.CommandHandler
 *
 * @author mical
 * @since 2023/8/16 1:33 PM
 */
@CommandHeader(name = "splendidenchants", aliases = ["se", "spe", "nereusopus", "no", "nereus"])
object CommandHandler : CommandHandler {

    override val sub: ConcurrentHashMap<String, CommandExecutor> = ConcurrentHashMap()
    val enchantNamesAndIds = mutableListOf<String>()

    @CommandBody
    val main = mainCommand {
        createTabooLibLegacyHelper()
    }

    @CommandBody(permission = "splendidenchants.book")
    val book = CommandBook.command

    @CommandBody(permission = "splendidenchants.enchant")
    val enchant = CommandEnchant.command

    @CommandBody(permission = "splendidenchants.random")
    val random = CommandRandom.command

    @CommandBody(permission = "splendidenchants.menu")
    val menu = CommandMenu.command

    @CommandBody(permission = "splendidenchants.reload")
    val reload = CommandReload.command
}