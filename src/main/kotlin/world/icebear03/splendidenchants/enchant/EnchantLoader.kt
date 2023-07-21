package world.icebear03.splendidenchants.enchant

import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import taboolib.common.io.newFolder
import taboolib.common.platform.function.getDataFolder
import taboolib.common.platform.function.info
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

    val keyMap = Enchantment::class.java.getProperty<HashMap<*, *>>("byKey", true)
    val nameMap = Enchantment::class.java.getProperty<HashMap<*, *>>("byName", true)

    fun resetSort() {
        enchantsByRarity.clear()
        enchantsByTarget.clear()
    }

    //enchants文件夹应该为若干文件夹，每个文件夹内为各个附魔配置
    fun initialize(reload: Boolean = false) {
        Enchantment::class.java.setProperty("acceptingNew", value = true, isStatic = true)
        val directory = File(getDataFolder(), "enchants/")
        if (!directory.exists()) directory.mkdirs()

        //记录重载时加载过的附魔
        val tmp = mutableSetOf<String>()

        newFolder(getDataFolder(), "enchants").listFiles { dir, _ -> dir.isDirectory }?.forEach { folder ->
            folder.listFiles()?.forEach { file ->
                val id = file.nameWithoutExtension
                if (reload && enchantById.containsKey(id)) {
                    val enchant = enchantById[id]!!
                    enchant.config.load()
                    tmp += id
                } else {
                    val key = NamespacedKey.fromString(id)!!
                    val enchant = SplendidEnchant(file, key)

                    // 注册附魔
                    if (!folder.name.equals("原版附魔")) {
                        keyMap?.remove(enchant.key)
                        nameMap?.remove(enchant.name)
                        Enchantment.registerEnchantment(enchant)
                    }
                    enchantById[id] = enchant
                    enchantByName[enchant.basicData.name] = enchant
                    enchantsByRarity[enchant.rarity]!! += enchant
                    enchant.targets.forEach {
                        enchantsByTarget[it]!! += enchant
                    }
                }
            }
        }

        //重载后已经被删除的附魔，需要取消注册
        if (reload) {
            enchantById.keys.filter { !tmp.contains(it) }.forEach {
                unregister(enchantById[it]!!)
            }
        }

        Enchantment::class.java.setProperty("acceptingNew", value = false, isStatic = true)

        info("    Successfully load ${enchantById.size} enchants!")
    }

    @Deprecated("useless")
    fun unregister() {
        enchantById.values.toList().forEach {
            unregister(it)
        }
    }

    fun unregister(enchant: SplendidEnchant) {
        if (!EnchantGroup.isIn(enchant, "原版附魔")) {
            keyMap?.remove(enchant.key)
            nameMap?.remove(enchant.name)
        }
        enchantById.remove(enchant.basicData.id)
        enchantByName.remove(enchant.basicData.name)
        enchantsByRarity[enchant.rarity]!!.remove(enchant)
        enchant.targets.forEach {
            enchantsByTarget[it]!!.remove(enchant)
        }
    }
}