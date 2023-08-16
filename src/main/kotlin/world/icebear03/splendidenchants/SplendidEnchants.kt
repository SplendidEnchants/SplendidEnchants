package world.icebear03.splendidenchants

import org.serverct.parrot.parrotx.mechanism.Reloadables
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.console
import taboolib.platform.util.onlinePlayers
import world.icebear03.splendidenchants.api.i18n.I18n
import world.icebear03.splendidenchants.api.internal.FurtherOperation
import world.icebear03.splendidenchants.api.internal.colorify
import world.icebear03.splendidenchants.enchant.EnchantDisplayer
import world.icebear03.splendidenchants.enchant.EnchantFilter
import world.icebear03.splendidenchants.enchant.EnchantLoader
import world.icebear03.splendidenchants.enchant.data.Group
import world.icebear03.splendidenchants.enchant.data.Rarity
import world.icebear03.splendidenchants.enchant.data.Target
import world.icebear03.splendidenchants.listener.mechanism.*
import kotlin.system.measureTimeMillis

object SplendidEnchants : Plugin() {

    override fun onEnable() {
        measureTimeMillis {
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
            console().sendMessage("Installing SplendidEnchants...")

            I18n.initialize() // 加载的提示信息已经内置在 I18n 类里了，不需要再单独写

            runCatching {
                console().sendMessage("|- Loading Config Module...")
                Config.load()

                console().sendMessage("|- Loading Enchants...")
                Rarity.load()
                Target.load()
                EnchantDisplayer.load()
                EnchantLoader.load()
                Group.load()

                console().sendMessage("|- Loading Mechanisms...")
                AnvilListener.load()
                AttainListener.load()
                GrindstoneListener.load()
                VillagerListener.load()
                ExpListener.load()

                FurtherOperation.load()

                console().sendMessage("|- Loading GUIs...")
                Reloadables.execute()
            }.onFailure {
                I18n.error(I18n.INIT, "SplendidEnchants", it)
            }
        }.let { time ->
            console().sendMessage("                            ")
            console().sendMessage("Installed SplendidEnchants in §6${time}ms")
            console().sendMessage("| Version: §r3.0.0")
            console().sendMessage("| Author: §f白熊_IceBear " + "&{#FFD0DB}Micalhl".colorify() + " §fxiaozhangup")
            console().sendMessage("| Tester: All players of §bStarLight§3繁星工坊")
        }
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
                EnchantLoader.load(true)
                Group.load()
                onlinePlayers.forEach { EnchantFilter.clearFilters(it) }

                console().sendMessage("|- Reloading Mechanisms...")
                AnvilListener.load()
                AttainListener.load()
                GrindstoneListener.load()
                VillagerListener.load()
                ExpListener.load()

                console().sendMessage("|- Reloading GUIs...")
                Reloadables.execute()
            }

        }.let { time ->
            console().sendMessage("                            ")
            console().sendMessage("Reloaded SplendidEnchants in §6${time}ms")
            console().sendMessage("| Version: §r3.0.0")
            console().sendMessage("| Author: §f白熊_IceBear " + "&{#FFD0DB}Micalhl".colorify() + " §fxiaozhangup")
            console().sendMessage("| Tester: All players of §bStarLight§3繁星工坊")
        }
    }
}