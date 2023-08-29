package world.icebear03.splendidenchants.enchant.mechanism.entry.internal

import world.icebear03.splendidenchants.enchant.mechanism.entry.`object`.*

abstract class ObjectEntry<E> : Entry() {

    abstract fun holderize(obj: E): Pair<ObjectEntry<E>, String>

    fun d(holder: Any?) = disholderize(holder.toString())

    abstract fun disholderize(holder: String): E?

    @Suppress("UNCHECKED_CAST")
    open fun m(
        obj: Any?,
        cmd: String,
        params: List<String>
    ): Boolean {
        return modify((obj as? E) ?: return false, cmd, params)
    }

    open fun modify(
        obj: E,
        cmd: String,
        params: List<String>
    ) = false

    @Suppress("UNCHECKED_CAST")
    fun g(from: Any?, objName: String): Pair<ObjectEntry<*>, Any?> {
        return get(from as? E ?: return objString to null, objName)
    }

    open operator fun get(from: E, objName: String): Pair<ObjectEntry<*>, Any?> = objString to null

    @Suppress("UNCHECKED_CAST")
    fun cast(obj: Any?): E? = obj as? E
}

val objString = ObjectString

val objBlock = ObjectBlock
val objEntity = ObjectEntity
val objLivingEntity = ObjectLivingEntity
val objPlayer = ObjectPlayer
val objItem = ObjectItem