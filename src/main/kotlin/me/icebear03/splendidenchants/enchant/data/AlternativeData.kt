package me.icebear03.splendidenchants.enchant.data;

data class AlternativeData(
    val grindstoneable: Boolean = true,
    val weight: Double = 1.0,
    val isTreasure: Boolean = false,
    val isCursed: Boolean = false,
    val isTradeable: Boolean = true,
    val isDiscoverable: Boolean = true
)