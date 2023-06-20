package me.icebear03.splendidenchants.enchant.data

import java.util.concurrent.ConcurrentHashMap

data class Rarity(
    val id: String,
    val name: String,
    val color: String,
    val weight: Double
) {

    init {
        TODO("INITIALIZE from rarity.yml")
    }

    companion object {

        val rarities = ConcurrentHashMap<String, Rarity>()
    }
}
// TODO: attainsources -> Tradable / Discoverable