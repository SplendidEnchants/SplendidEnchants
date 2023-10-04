package world.icebear03.splendidenchants

import com.mcstarrysky.starrysky.i18n.I18n
import me.arasple.mc.trchat.module.internal.hook.HookPlugin
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.serverct.parrot.parrotx.mechanism.Reloadables
import org.serverct.parrot.parrotx.ui.registry.MenuFunctions
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.console
import taboolib.platform.BukkitPlugin
import taboolib.platform.util.onlinePlayers
import world.icebear03.splendidenchants.api.internal.FurtherOperation
import world.icebear03.splendidenchants.api.internal.colorify
import world.icebear03.splendidenchants.enchant.EnchantDisplayer
import world.icebear03.splendidenchants.enchant.EnchantFilter
import world.icebear03.splendidenchants.enchant.EnchantLoader
import world.icebear03.splendidenchants.enchant.data.Group
import world.icebear03.splendidenchants.enchant.data.Rarity
import world.icebear03.splendidenchants.enchant.data.Target
import world.icebear03.splendidenchants.enchant.data.limitation.Limitations
import world.icebear03.splendidenchants.enchant.mechanism.Tickers
import world.icebear03.splendidenchants.listener.mechanism.*
import world.icebear03.splendidenchants.player.DataLoader
import world.icebear03.splendidenchants.player.saveSEData
import world.icebear03.splendidenchants.supports.HookInteractiveChat
import world.icebear03.splendidenchants.supports.HookProtocolLib
import world.icebear03.splendidenchants.supports.HookTrChat
import world.icebear03.splendidenchants.ui.internal.back
import kotlin.system.measureTimeMillis

object SplendidEnchants : Plugin() {
    val plugin: JavaPlugin by lazy { BukkitPlugin.getInstance() }

    override fun onActive() {
        console().sendMessage("Loading supports for other plugins...")
        if (Bukkit.getPluginManager().isPluginEnabled("TrChat")) {
            console().sendMessage("|- TrChat detected, attempt to hook it...")
            HookPlugin.addHook(HookTrChat)
        }
        if (Bukkit.getPluginManager().isPluginEnabled("InteractiveChat")) {
            console().sendMessage("|- InteractiveChat detected, attempt to hook it...")
            HookInteractiveChat.load()
        }
        if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            console().sendMessage("|- ProtocolLib detected, attempt to hook it...")
            HookProtocolLib.load()
        }
    }

    override fun onEnable() {
        measureTimeMillis {
            sendLogo();
            console().sendMessage("Installing SplendidEnchants...")

            I18n.initialize() // 加载的提示信息已经内置在 I18n 类里了，不需要再单独写

            runCatching {
                console().sendMessage("|- Loading Config Module...")
                Config.load()

                console().sendMessage("|- Loading Enchants...")
                Rarity.load()
                Target.load()
                EnchantDisplayer.load()
                Tickers.load()
                EnchantLoader.load()
                Group.load()

                Limitations.initConflicts()

                console().sendMessage("|- Loading Mechanisms...")
                AnvilListener.load()
                AttainListener.load()
                GrindstoneListener.load()
                VillagerListener.load()
                ExpListener.load()

                FurtherOperation.load()

                console().sendMessage("|- Loading GUIs...")
                MenuFunctions.unregister("Back")
                MenuFunctions.register("Back", false) { back }
                Reloadables.execute()

                DataLoader.load()
                AntiIllegalItem.load()
            }.onFailure {
                I18n.error(I18n.INIT, "SplendidEnchants", it)
            }
        }.let { time ->
            console().sendMessage("                            ")
            console().sendMessage("Installed SplendidEnchants in §6${time}ms")
            sendInfo()
        }
    }

    override fun onDisable() {
        if (Bukkit.getPluginManager().isPluginEnabled("TrChat"))
            HookPlugin.registry.removeIf { it.plugin?.name == "SplendidEnchants" }
        onlinePlayers.forEach { it.saveSEData() }
        EnchantLoader.unregisterAll()
    }

    fun reload() {
        console().sendMessage("Reloading SplendidEnchants...")

        measureTimeMillis {
            runCatching {
                I18n.reload()

                console().sendMessage("|- Reloading Config Module...")
                Config.load()

                console().sendMessage("|- Reloading Enchants...")
                EnchantLoader.resetSort()
                Rarity.load()
                Target.load()
                EnchantDisplayer.load()
                Tickers.load()
                EnchantLoader.load(true)
                Group.load()

                Limitations.initConflicts()
                onlinePlayers.forEach { EnchantFilter.clearFilters(it) }

                console().sendMessage("|- Reloading Mechanisms...")
                AnvilListener.load()
                AttainListener.load()
                GrindstoneListener.load()
                VillagerListener.load()
                ExpListener.load()

                console().sendMessage("|- Reloading GUIs...")
                MenuFunctions.unregister("Back")
                MenuFunctions.register("Back", false) { back }
                Reloadables.execute()

                AntiIllegalItem.load()
            }.onFailure {
                I18n.error(I18n.INIT, "SplendidEnchants", it)
            }
        }.let { time ->
            console().sendMessage("                            ")
            console().sendMessage("Reloaded SplendidEnchants in §6${time}ms")
            sendInfo()
        }
    }

    private fun sendInfo() {
        console().sendMessage("| Version: §r3.0.0")
        console().sendMessage("| Author: §f白熊_IceBear " + "&{#FFD0DB}Micalhl".colorify() + " §fxiaozhangup")
        console().sendMessage("| Tester: All players of §bStarLight§3繁星工坊 §eHAPPYLAND")
    }

    private fun sendLogo() {
        console().sendMessage("§e                                                               ")
        console().sendMessage("§e ______   ______  __       ______   __   __   _____    __   _____                         ")
        console().sendMessage("§e/\\  ___\\ /\\  == \\/\\ \\     /\\  ___\\ /\\ \"-.\\ \\ /\\  __-. /\\ \\ /\\  __-.                       ")
        console().sendMessage("§e\\ \\___  \\\\ \\  _-/\\ \\ \\____\\ \\  __\\ \\ \\ \\-.  \\\\ \\ \\/\\ \\\\ \\ \\\\ \\ \\/\\ \\                      ")
        console().sendMessage("§e \\/\\_____\\\\ \\_\\   \\ \\_____\\\\ \\_____\\\\ \\_\\\\\"\\_\\\\ \\____- \\ \\_\\\\ \\____-                      ")
        console().sendMessage("§e  \\/_____/ \\/_/    \\/_____/ \\/_____/ \\/_/ \\/_/ \\/____/  \\/_/ \\/____/                      ")
        console().sendMessage("§e                                                                                          ")
        console().sendMessage("§e                ______   __   __   ______   __  __   ______   __   __   ______  ______    ")
        console().sendMessage("§e               /\\  ___\\ /\\ \"-.\\ \\ /\\  ___\\ /\\ \\_\\ \\ /\\  __ \\ /\\ \"-.\\ \\ /\\__  _\\/\\  ___\\   ")
        console().sendMessage("§e               \\ \\  __\\ \\ \\ \\-.  \\\\ \\ \\____\\ \\  __ \\\\ \\  __ \\\\ \\ \\-.  \\\\/_/\\ \\/\\ \\___  \\  ")
        console().sendMessage("§e                \\ \\_____\\\\ \\_\\\\\"\\_\\\\ \\_____\\\\ \\_\\ \\_\\\\ \\_\\ \\_\\\\ \\_\\\\\"\\_\\  \\ \\_\\ \\/\\_____\\ ")
        console().sendMessage("§e                 \\/_____/ \\/_/ \\/_/ \\/_____/ \\/_/\\/_/ \\/_/\\/_/ \\/_/ \\/_/   \\/_/  \\/_____/ ")
        console().sendMessage("§e                                                               ")
    }
}