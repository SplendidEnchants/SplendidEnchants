package world.icebear03.splendidenchants.command

import org.bukkit.entity.Player
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.command.PermissionDefault
import taboolib.common.platform.command.command
import world.icebear03.splendidenchants.command.impl.CommandMenu

object CommandPlayer {

    @Awake(LifeCycle.ENABLE)
    fun regCommand() {
        command("enchants", permissionDefault = PermissionDefault.TRUE, permission = "splendidenchants.enchants") {
            execute<Player> { sender, _, _ -> CommandMenu.handle(sender) }
            dynamic {
                suggestion<Player>(uncheck = true) { _, _ -> CommandMenu.menus }
                execute<Player> { sender, _, argument ->
                    CommandMenu.handle(sender, argument)
                }
            }
        }
    }

}