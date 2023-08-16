package world.icebear03.splendidenchants.enchant

import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import taboolib.common.io.runningResources
import taboolib.common.platform.function.console
import taboolib.common.platform.function.getDataFolder
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.library.reflex.Reflex.Companion.setProperty
import world.icebear03.splendidenchants.api.internal.YamlUpdater
import world.icebear03.splendidenchants.api.internal.error.missingConfig
import world.icebear03.splendidenchants.command.CommandHandler
import world.icebear03.splendidenchants.enchant.data.Rarity
import world.icebear03.splendidenchants.enchant.data.Target
import world.icebear03.splendidenchants.enchant.data.isIn
import java.io.File

object EnchantLoader {

    //id-enchant
    val BY_ID = mutableMapOf<String, SplendidEnchant>()

    //displayname-enchant
    val BY_NAME = mutableMapOf<String, SplendidEnchant>()
    val BY_RARITY = mutableMapOf<Rarity, MutableList<SplendidEnchant>>()
    val BY_TARGET = mutableMapOf<Target, MutableList<SplendidEnchant>>()

    //id key-enchant
    private val keyMap = Enchantment::class.java.getProperty<HashMap<*, *>>("byKey", true)!!

    //id-enchant
    private val nameMap = Enchantment::class.java.getProperty<HashMap<*, *>>("byName", true)!!

    fun resetSort() {
        BY_RARITY.clear()
        BY_TARGET.clear()
    }

    //enchants文件夹应该为若干文件夹，每个文件夹内为各个附魔配置
    fun load(reload: Boolean = false) {
        //注册附魔的准备工作
        Enchantment::class.java.setProperty("acceptingNew", value = true, isStatic = true)

        //输出所有附魔的文件
        releaseEnchantFiles()
        val directory = File(getDataFolder(), "enchants/")

        //记录重载时加载过的附魔
        val loaded = mutableSetOf<String>()

        directory.listFiles { dir, _ -> dir.isDirectory }?.forEach { folder ->
            folder.listFiles()?.forEach { file ->
                //因此，文件名必须与basic.id一致
                val id = file.nameWithoutExtension
                if (reload && BY_ID.containsKey(id)) {
                    BY_ID[id]!!.config.load()?.let { missingConfig(file, it) }
                    loaded += id
                } else {
                    val key = NamespacedKey.minecraft(id)
                    val enchant = SplendidEnchant(file, key)

                    // 注册附魔
                    if (folder.name != "原版附魔") {
                        keyMap.remove(key)
                        nameMap.remove(id)
                        Enchantment.registerEnchantment(enchant)
                    }

                    //添加map entry
                    BY_ID[id] = enchant
                    BY_NAME[enchant.basicData.name] = enchant
                    BY_RARITY.getOrPut(enchant.rarity) { mutableListOf() } += enchant
                    enchant.targets.forEach { target ->
                        BY_TARGET.getOrPut(target) { mutableListOf() } += enchant
                    }
                }
            }
        }

        //重载后已经被删除的附魔，需要取消注册
        if (reload) BY_ID.keys.filter { !loaded.contains(it) }.forEach { unregister(it) }
        //还原附魔注册设定
        Enchantment::class.java.setProperty("acceptingNew", value = false, isStatic = true)
        //重新生成TabList
        CommandHandler.enchantNamesAndIds.clear()
        CommandHandler.enchantNamesAndIds.addAll(BY_ID.map { it.key } + BY_NAME.map { it.key })

        console().sendMessage("    Successfully load §6${BY_ID.size} enchants")
    }

    fun releaseEnchantFiles() {
        runningResources.filter {
            it.endsWith(".yml")
                    && it.startsWith("enchants/")
                    && it.count { c -> c == '/' } == 2
        }.forEach {
            YamlUpdater.loadAndUpdate(it)
        }
    }

    private fun unregister(id: String) {
        val enchant = BY_ID[id] ?: return
        if (!enchant.isIn("原版附魔")) {
            keyMap.remove(enchant.key)
            nameMap.remove(enchant.name)
        }
        BY_ID.remove(enchant.basicData.id)
        BY_NAME.remove(enchant.basicData.name)
        BY_RARITY[enchant.rarity]!!.remove(enchant)
        enchant.targets.forEach { target ->
            BY_TARGET[target]!!.remove(enchant)
        }
    }
}