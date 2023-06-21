package me.icebear03.splendidenchants.api

import org.bukkit.entity.Player

object PlayerAPI {

    fun convertPlaceHolders(string: String, player: Player): String {
        val result = string
        // TODO: 添加较为常用的几个
        // TODO: 走placeholderapi（如果安装）
        return result
    }
}