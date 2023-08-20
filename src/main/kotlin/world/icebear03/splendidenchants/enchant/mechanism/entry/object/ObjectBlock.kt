package world.icebear03.splendidenchants.enchant.mechanism.entry.`object`

import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.data.Ageable
import org.bukkit.entity.Player
import world.icebear03.splendidenchants.api.*
import world.icebear03.splendidenchants.api.internal.FurtherOperation

object ObjectBlock {

    val crops = mapOf(
        Material.WHEAT to Material.WHEAT,
        Material.PUMPKIN to Material.PUMPKIN,
        Material.MELON to Material.MELON,
        Material.BEETROOTS to Material.BEETROOT,
        Material.CARROTS to Material.CARROT,
        Material.POTATOES to Material.POTATO,
        Material.SWEET_BERRIES to Material.SWEET_BERRIES,
        Material.GLOW_BERRIES to Material.GLOW_BERRIES
    )

    fun modify(
        block: Block,
        params: List<String>,
        holders: MutableMap<String, Any>
    ): Boolean {

        holders["方块类型"] = block.type
        holders["方块生物群系"] = block.biome
        holders["方块充能等级"] = block.blockPower
        holders["方块坐标x"] = block.x
        holders["方块坐标y"] = block.y
        holders["方块坐标z"] = block.z

        holders["是否为农作物"] = crops[block.type]?.let { holders["农作物物品类型"] = it;true } ?: false
        val data = block.blockData
        if (data is Ageable) holders["年龄"] = data.age

        val variabled = params.map { it.replace(holders) }
        val type = variabled[0]
        val after = variabled.subList(1)

        when (type) {
            "破坏" -> FurtherOperation.furtherBreak(holders["玩家"] as Player, block)
            "放置" -> FurtherOperation.furtherPlace(holders["玩家"] as Player, block, holders["方块类型"] as Material)
            "生成半径缓存" -> {
                val range = after[0].toInt()
                for (x in -range..range)
                    for (y in -range..range)
                        for (z in -range..range) {
                            holders["($x,$y,$z)方块"] = block.location.let { it.add(x, y, z); it.block }
                        }
            }

            "设置年龄" -> (data as? Ageable)?.let {
                it.age = after[0].calcToInt().coerceAtLeast(0).coerceAtMost(it.maximumAge)
                block.blockData = it
            }

            "临时方块" -> {
                val player = holders[after[0]]!! as Player
                val material = Material.valueOf(after[1])
                val duration = after.getOrElse(2) { "0.5" }.calcToDouble()
                player.tmpBlock(block.location, material, duration)
            }

            else -> return false
        }
        return true
    }
}