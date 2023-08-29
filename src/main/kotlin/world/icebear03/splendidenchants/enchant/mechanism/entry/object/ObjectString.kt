package world.icebear03.splendidenchants.enchant.mechanism.entry.`object`

import world.icebear03.splendidenchants.enchant.mechanism.entry.internal.ObjectEntry

object ObjectString : ObjectEntry<String>() {

    fun holderize(obj: Any?) = this to obj.toString()

    override fun holderize(obj: String) = this to obj
    override fun disholderize(holder: String) = holder
}