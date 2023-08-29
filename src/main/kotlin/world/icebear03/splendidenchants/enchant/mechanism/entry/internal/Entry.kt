package world.icebear03.splendidenchants.enchant.mechanism.entry.internal

abstract class Entry {

    private val objs = mutableMapOf<String, ObjectEntry<*>>()

    fun registerObj(objName: String, type: ObjectEntry<*>) = objs.put(objName, type)

    fun getObjType(objName: String): ObjectEntry<*>? = objs[objName]
}