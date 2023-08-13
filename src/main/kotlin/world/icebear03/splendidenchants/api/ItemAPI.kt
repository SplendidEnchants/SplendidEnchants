package world.icebear03.splendidenchants.api

import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
import org.serverct.parrot.parrotx.function.textured
import taboolib.platform.util.modifyMeta
import world.icebear03.splendidenchants.enchant.SplendidEnchant

var ItemStack.name
    get() = itemMeta?.displayName
    set(value) {
        modifyMeta<ItemMeta> { setDisplayName(value) }
    }

var ItemStack.damage
    get() = (itemMeta as? Damageable)?.damage ?: 0
    set(value) {
        modifyMeta<Damageable> { damage = value }
    }

var ItemMeta.fixedEnchants
    get(): Map<SplendidEnchant, Int> {
        return (if (this is EnchantmentStorageMeta) storedEnchants
        else enchants).mapKeys { (enchant, _) -> enchant.splendidEt() }
    }
    set(value) {
        clearEts()
        if (this is EnchantmentStorageMeta) storedEnchants.putAll(value)
        else enchants.putAll(value)
    }

var ItemStack.fixedEnchants
    get(): Map<SplendidEnchant, Int> = itemMeta?.fixedEnchants ?: emptyMap()
    set(value) {
        modifyMeta<ItemMeta> { fixedEnchants = value }
    }

fun ItemMeta.etLevel(enchant: SplendidEnchant) = fixedEnchants[enchant] ?: -1

fun ItemStack.etLevel(enchant: SplendidEnchant) = itemMeta.etLevel(enchant)

fun ItemMeta.addEt(enchant: SplendidEnchant, level: Int = enchant.maxLevel) {
    if (this is EnchantmentStorageMeta) storedEnchants[enchant] = level
    else enchants[enchant] = level
}

fun ItemStack.addEt(enchant: SplendidEnchant, level: Int = enchant.maxLevel) {
    modifyMeta<ItemMeta> { addEt(enchant, level) }
}

fun ItemMeta.removeEt(enchant: SplendidEnchant) {
    if (this is EnchantmentStorageMeta) removeStoredEnchant(enchant)
    else removeEnchant(enchant)
}

fun ItemStack.removeEt(enchant: SplendidEnchant) {
    modifyMeta<ItemMeta> { removeEt(enchant) }
}

fun ItemMeta.clearEts() {
    if (this is EnchantmentStorageMeta) storedEnchants.clear()
    else enchants.clear()
}

fun ItemStack.clearEts() {
    modifyMeta<ItemMeta> { clearEts() }
}

fun ItemStack.skull(skull: String) {
    if (skull.length <= 20) modifyMeta<SkullMeta> { owner = skull }
    else textured(skull)
}

val ItemStack.isEnchantedBook get() = itemMeta is EnchantmentStorageMeta