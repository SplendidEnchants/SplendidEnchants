package me.icebear03.splendidenchants.enchant.mechanisms

data class Displayer(
    val generalDescription: String,
    val leveledDescription: String,
    val displayFormat: String
)

// TODO: 读取 display.yml 中的 $default
// TODO: 根据不同等级、不同状态（TODO-ItemStack PDC储存）生成 description 和 display