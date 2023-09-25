package world.icebear03.splendidenchants

import com.mcstarrysky.starrysky.i18n.I18n
import me.arasple.mc.trchat.module.internal.hook.HookPlugin
import org.bukkit.Bukkit
import org.serverct.parrot.parrotx.mechanism.Reloadables
import org.serverct.parrot.parrotx.ui.registry.MenuFunctions
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.info
import taboolib.platform.util.onlinePlayers
import world.icebear03.splendidenchants.api.internal.FurtherOperation
import world.icebear03.splendidenchants.api.internal.colorify
import world.icebear03.splendidenchants.enchant.EnchantDisplayer
import world.icebear03.splendidenchants.enchant.EnchantFilter
import world.icebear03.splendidenchants.enchant.EnchantLoader
import world.icebear03.splendidenchants.enchant.data.Group
import world.icebear03.splendidenchants.enchant.data.Rarity
import world.icebear03.splendidenchants.enchant.data.Target
import world.icebear03.splendidenchants.enchant.mechanism.Tickers
import world.icebear03.splendidenchants.listener.mechanism.*
import world.icebear03.splendidenchants.player.DataLoader
import world.icebear03.splendidenchants.player.saveSEData
import world.icebear03.splendidenchants.supports.HookInteractiveChat
import world.icebear03.splendidenchants.supports.HookTrChat
import world.icebear03.splendidenchants.ui.internal.back
import kotlin.system.measureTimeMillis

object SplendidEnchants : Plugin() {
    override fun onActive() {
        info("Loading supports for other plugins...")
        if (Bukkit.getPluginManager().isPluginEnabled("TrChat")) {
            info("|- TrChat detected, attempt to hook it...")
            HookPlugin.addHook(HookTrChat)
        }
        if (Bukkit.getPluginManager().isPluginEnabled("InteractiveChat")) {
            info("|- InteractiveChat detected, attempt to hook it...")
            HookInteractiveChat.load()
        }
    }

    override fun onEnable() {
        measureTimeMillis {
            sendLogo();
            info("Installing SplendidEnchants...")

            I18n.initialize() // 加载的提示信息已经内置在 I18n 类里了，不需要再单独写

            runCatching {
                info("|- Loading Config Module...")
                Config.load()

                info("|- Loading Enchants...")
                Rarity.load()
                Target.load()
                EnchantDisplayer.load()
                Tickers.load()
                EnchantLoader.load()
                Group.load()

                info("|- Loading Mechanisms...")
                AnvilListener.load()
                AttainListener.load()
                GrindstoneListener.load()
                VillagerListener.load()
                ExpListener.load()

                FurtherOperation.load()

                info("|- Loading GUIs...")
                MenuFunctions.unregister("Back")
                MenuFunctions.register("Back", false) { back }
                Reloadables.execute()

                DataLoader.load()
            }.onFailure {
                I18n.error(I18n.INIT, "SplendidEnchants", it)
            }
        }.let { time ->
            info("                            ")
            info("Installed SplendidEnchants in §6${time}ms")
            info("| Version: §r3.0.0")
            info("| Author: §f白熊_IceBear " + "&{#FFD0DB}Micalhl".colorify() + " §fxiaozhangup")
            info("| Tester: All players of §bStarLight§3繁星工坊")
        }
    }

    override fun onDisable() {
        if (Bukkit.getPluginManager().isPluginEnabled("TrChat"))
            HookPlugin.registry.removeIf { it.plugin?.name == "SplendidEnchants" }
        onlinePlayers.forEach { it.saveSEData() }
        EnchantLoader.unregisterAll()
    }

    fun reload() {
        info("Reloading SplendidEnchants...")

        measureTimeMillis {
            runCatching {
                I18n.reload()

                info("|- Reloading Config Module...")
                Config.load()

                info("|- Reloading Enchants...")
                EnchantLoader.resetSort()
                Rarity.load()
                Target.load()
                EnchantDisplayer.load()
                Tickers.load()
                EnchantLoader.load(true)
                Group.load()
                onlinePlayers.forEach { EnchantFilter.clearFilters(it) }

                info("|- Reloading Mechanisms...")
                AnvilListener.load()
                AttainListener.load()
                GrindstoneListener.load()
                VillagerListener.load()
                ExpListener.load()

                info("|- Reloading GUIs...")
                MenuFunctions.unregister("Back")
                MenuFunctions.register("Back", false) { back }
                Reloadables.execute()
            }.onFailure {
                I18n.error(I18n.INIT, "SplendidEnchants", it)
            }
        }.let { time ->
            info("                            ")
            info("Reloaded SplendidEnchants in §6${time}ms")
            info("| Version: §r3.0.0")
            info("| Author: §f白熊_IceBear " + "&{#FFD0DB}Micalhl".colorify() + " §fxiaozhangup")
            info("| Tester: All players of §bStarLight§3繁星工坊")
        }
    }

    fun sendLogo() {
        info("§e                                                               ")
        info("§e ______   ______  __       ______   __   __   _____    __   _____                         ")
        info("§e/\\  ___\\ /\\  == \\/\\ \\     /\\  ___\\ /\\ \"-.\\ \\ /\\  __-. /\\ \\ /\\  __-.                       ")
        info("§e\\ \\___  \\\\ \\  _-/\\ \\ \\____\\ \\  __\\ \\ \\ \\-.  \\\\ \\ \\/\\ \\\\ \\ \\\\ \\ \\/\\ \\                      ")
        info("§e \\/\\_____\\\\ \\_\\   \\ \\_____\\\\ \\_____\\\\ \\_\\\\\"\\_\\\\ \\____- \\ \\_\\\\ \\____-                      ")
        info("§e  \\/_____/ \\/_/    \\/_____/ \\/_____/ \\/_/ \\/_/ \\/____/  \\/_/ \\/____/                      ")
        info("§e                                                                                          ")
        info("§e                ______   __   __   ______   __  __   ______   __   __   ______  ______    ")
        info("§e               /\\  ___\\ /\\ \"-.\\ \\ /\\  ___\\ /\\ \\_\\ \\ /\\  __ \\ /\\ \"-.\\ \\ /\\__  _\\/\\  ___\\   ")
        info("§e               \\ \\  __\\ \\ \\ \\-.  \\\\ \\ \\____\\ \\  __ \\\\ \\  __ \\\\ \\ \\-.  \\\\/_/\\ \\/\\ \\___  \\  ")
        info("§e                \\ \\_____\\\\ \\_\\\\\"\\_\\\\ \\_____\\\\ \\_\\ \\_\\\\ \\_\\ \\_\\\\ \\_\\\\\"\\_\\  \\ \\_\\ \\/\\_____\\ ")
        info("§e                 \\/_____/ \\/_/ \\/_/ \\/_____/ \\/_/\\/_/ \\/_/\\/_/ \\/_/ \\/_/   \\/_/  \\/_____/ ")
        info("§e                                                               ")
    }
}