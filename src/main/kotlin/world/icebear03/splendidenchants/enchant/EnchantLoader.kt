package world.icebear03.splendidenchants.enchant

import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import taboolib.common.io.newFolder
import taboolib.common.platform.function.getDataFolder
import taboolib.library.reflex.Reflex.Companion.getProperty
import taboolib.library.reflex.Reflex.Companion.setProperty
import world.icebear03.splendidenchants.enchant.data.Rarity
import world.icebear03.splendidenchants.enchant.data.Target
import java.io.File

object EnchantLoader {

    val enchantById = mutableMapOf<String, SplendidEnchant>()
    val enchantByName = mutableMapOf<String, SplendidEnchant>()
    val enchantsByRarity = mutableMapOf<Rarity, MutableSet<SplendidEnchant>>()
    val enchantsByTarget = mutableMapOf<Target, MutableSet<SplendidEnchant>>()

    //enchants文件夹应该为若干文件夹，每个文件夹内为各个附魔配置
    fun initialize() {
        Enchantment::class.java.setProperty("acceptingNew", value = true, isStatic = true)
        val directory = File(getDataFolder(), "enchants/")
        if (!directory.exists()) directory.mkdirs()
        newFolder(getDataFolder(), "enchants").listFiles { dir, _ -> dir.isDirectory }?.forEach { folder ->
            folder.listFiles()?.forEach { file ->
                val id = file.nameWithoutExtension
                val key = NamespacedKey.fromString(id)!!
                val enchant = SplendidEnchant(file, key)

                // 注册附魔
                if (!folder.name.equals("原版附魔"))
                    Enchantment.registerEnchantment(enchant)
                enchantById[id] = enchant
                enchantByName[enchant.basicData.name] = enchant
                enchantsByRarity[enchant.rarity]!! += enchant
                enchant.targets.forEach {
                    enchantsByTarget[it]!! += enchant
                }
            }
        }
        Enchantment::class.java.setProperty("acceptingNew", value = false, isStatic = true)
    }

    fun unregister() {
        enchantById.values.forEach {
            if (!EnchantGroup.isIn(it, "原版附魔")) {
                val keyMap = Enchantment::class.java.getProperty<HashMap<*, *>>("byKey", true)
                val nameMap = Enchantment::class.java.getProperty<HashMap<*, *>>("byName", true)
                keyMap?.remove(it.key)
                nameMap?.remove(it.name)
            }
        }
    }
}