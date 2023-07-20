package world.icebear03.splendidenchants.command

import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.expansion.createHelper
import world.icebear03.splendidenchants.command.sub.commandBook
import world.icebear03.splendidenchants.command.sub.commandEnchant
import world.icebear03.splendidenchants.command.sub.commandMenu
import world.icebear03.splendidenchants.command.sub.commandRandom
import world.icebear03.splendidenchants.enchant.EnchantLoader

@CommandHeader(name = "splendidenchants", aliases = ["se", "spe", "nereusopus", "no", "nerous"])
object Commands {

    val enchantNamesAndIds = EnchantLoader.enchantById.keys.toList().toMutableList().also {
        it.addAll(EnchantLoader.enchantByName.keys.toList())
    }

    @CommandBody
    val main = mainCommand {
        createHelper(checkPermissions = true)
    }

    @CommandBody(permission = "splendidenchants.book")
    val book = commandBook

    @CommandBody(permission = "splendidenchants.enchant")
    val enchant = commandEnchant

    @CommandBody(permission = "splendidenchants.random")
    val random = commandRandom

    @CommandBody(permission = "splendidenchants.menu")
    val menu = commandMenu
}