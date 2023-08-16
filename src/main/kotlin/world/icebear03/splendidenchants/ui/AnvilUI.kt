package world.icebear03.splendidenchants.ui

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.serverct.parrot.parrotx.function.variables
import org.serverct.parrot.parrotx.mechanism.Reloadable
import org.serverct.parrot.parrotx.ui.MenuComponent
import org.serverct.parrot.parrotx.ui.config.MenuConfiguration
import org.serverct.parrot.parrotx.ui.feature.util.MenuFunctionBuilder
import taboolib.module.chat.colored
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.isAir
import world.icebear03.splendidenchants.api.fixedEnchants
import world.icebear03.splendidenchants.api.initialize
import world.icebear03.splendidenchants.api.setSlots
import world.icebear03.splendidenchants.enchant.data.limitation.CheckType
import world.icebear03.splendidenchants.listener.mechanism.AnvilListener

@MenuComponent("Anvil")
object AnvilUI {

    @Config("gui/anvil.yml")
    private lateinit var source: Configuration
    private lateinit var config: MenuConfiguration

    @Reloadable
    fun reload() {
        source.reload()
        config = MenuConfiguration(source)
    }

    fun open(player: Player, a: ItemStack? = null, b: ItemStack? = null) {
        player.openMenu<Basic>(config.title().colored()) {
            val (shape, templates) = config
            rows(shape.rows)
            map(*shape.array)

            initialize(shape, templates, "Anvil:a", "Anvil:b", "Anvil:result", "Anvil:information")

            val info = mutableMapOf<String, String>()
            var result: ItemStack? = ItemStack(Material.AIR)
            if (a != null && b != null && !a.isAir && !b.isAir) {
                val resultPair = AnvilListener.anvil(a, b, player)
                result = resultPair.first
                val cost = resultPair.second

                val canCombine = if (result == null || cost <= 0) false
                else if (result.isSimilar(a)) false
                else true

                info.putAll(
                    if (canCombine) listOf("allowed" to "&a允许", "level" to "$cost", "reasons" to "无")
                    else {
                        val bugs = b.fixedEnchants.mapNotNull { (enchant, _) ->
                            val check = enchant.limitations.checkAvailable(CheckType.ANVIL, a, player)
                            if (!check.first) check.second
                            else null
                        }
                        listOf(
                            "allowed" to "&c不允许",
                            "level" to "N/A",
                            "reasons" to (bugs + "" + "其他可能:" + "拼合前后物品不变" + "经验值消耗小于等于0级").joinToString("||")
                        )
                    }
                )
            }

            listOf("a", "b", "result", "information").forEach {
                setSlots(
                    shape, templates, "Anvil:$it", listOf(),
                    "a" to (a ?: ItemStack(Material.AIR)), "b" to (b ?: ItemStack(Material.AIR)),
                    "result" to (result ?: ItemStack(Material.AIR)), "info" to info
                )
            }

            onClick { event ->
                event.isCancelled = true
                if (event.rawSlot in shape)
                    templates[event.rawSlot]?.handle(this, event)
                if (event.rawSlot !in shape) {
                    val item = event.currentItem ?: return@onClick
                    if (a == null) open(player, item, b)
                    else if (b == null) open(player, a, item)
                }
            }
        }
    }

    @MenuComponent
    private val a = MenuFunctionBuilder {
        onBuild { (_, _, _, _, icon, args) -> (args["a"] as ItemStack).takeIf { it.isAir } ?: icon }
        onClick { (_, _, _, event, args) -> open(event.clicker, null, args["b"] as ItemStack) }
    }

    @MenuComponent
    private val b = MenuFunctionBuilder {
        onBuild { (_, _, _, _, icon, args) -> (args["b"] as ItemStack).takeIf { it.isAir } ?: icon }
        onClick { (_, _, _, event, args) -> open(event.clicker, args["a"] as ItemStack, null) }
    }

    @MenuComponent
    private val result = MenuFunctionBuilder {
        onBuild { (_, _, _, _, icon, args) -> (args["result"] as ItemStack).takeIf { it.isAir } ?: icon }
    }

    @MenuComponent
    private val information = MenuFunctionBuilder {
        onBuild { (_, _, _, _, icon, args) ->
            val tmp = args["info"] as Map<*, *>
            val info = tmp.mapKeys { it.toString() }.mapValues { it.toString() }
            if (info.isEmpty()) icon.variables { listOf("-") }
            else icon.variables {
                when (it) {
                    "allowed" -> listOf(info["allowed"]!!)
                    "level" -> listOf(info["level"]!!)
                    "reasons" -> info["reasons"]!!.split("||")
                    else -> listOf()
                }
            }
        }
    }
}