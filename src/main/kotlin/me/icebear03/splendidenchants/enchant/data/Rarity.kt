package me.icebear03.splendidenchants.enchant.data

class Rarity(private val id: String, private val name: String, private val color: String, private val weight: Double) {
    fun id(): String {
        return id
    }

    fun name(): String {
        return name
    }

    fun color(): String {
        return color
    }

    fun weight(): Double {
        return weight
    } //TODO attainsources在各个配置文件处理
}
