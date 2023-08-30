package world.icebear03.splendidenchants.enchant.mechanism.entry.`object`

import world.icebear03.splendidenchants.enchant.mechanism.entry.internal.ObjectEntry

object ObjectString : ObjectEntry<String>() {

    override fun holderize(obj: String) = this to obj
    override fun disholderize(holder: String) = holder
}