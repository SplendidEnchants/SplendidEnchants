package world.icebear03.splendidenchants.util

import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataHolder
import org.bukkit.persistence.PersistentDataType

operator fun <T, Z> PersistentDataHolder.get(key: String, type: PersistentDataType<T, Z>): Z? {
    return persistentDataContainer.get(NamespacedKey.minecraft(key), type)
}

operator fun <T, Z : Any> PersistentDataHolder.set(key: String, type: PersistentDataType<T, Z>, value: Z) {
    persistentDataContainer.set(NamespacedKey.minecraft(key), type, value)
}


fun <T, Z : Any> PersistentDataHolder.has(key: String, type: PersistentDataType<T, Z>): Boolean {
    return persistentDataContainer.has(NamespacedKey.minecraft(key), type)
}

fun PersistentDataHolder.remove(key: String) {
    return persistentDataContainer.remove(NamespacedKey.minecraft(key))
}