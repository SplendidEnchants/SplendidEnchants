package me.icebear03.splendidenchants.enchant

import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import taboolib.common.platform.function.getDataFolder
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.library.reflex.Reflex.Companion.setProperty
import java.io.File
import java.util.concurrent.ConcurrentHashMap

object EnchantLoader {
    val enchantById = ConcurrentHashMap<String, SplendidEnchant>()

    //enchants文件夹应该为若干文件夹，每个文件夹内为各个附魔配置
    fun initialize() {
        Enchantment::class.java.setProperty("acceptingNew", value = true, isStatic = true)
        val directory = File(getDataFolder(), "enchants/")
        if (!directory.exists()) directory.mkdirs()
        getListFiles(directory).forEach {
            getListFiles(it).forEach { file ->
                val id = file.name.replace(".yml", "")
                val key = NamespacedKey.fromString(id)!!
                val enchant = SplendidEnchant(file, key)

                //注册附魔
                Enchantment.registerEnchantment(enchant)
                enchantById[id] = enchant
            }
        }
    }

    fun unregister() {
        enchantById.values.forEach {
            val keyMap = Enchantment::class.java.getProperty<HashMap<*, *>>("byKey", true)
            val nameMap = Enchantment::class.java.getProperty<HashMap<*, *>>("byName", true)
            keyMap?.remove(it.key)
            nameMap?.remove(it.name)
        }
    }

    fun getListFiles(directory: File): List<File> {
        val result = ArrayList<File>()
        if (directory.listFiles() != null)
            result.addAll(directory.listFiles()!!)
        return result
    }
}