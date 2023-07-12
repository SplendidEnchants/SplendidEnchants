package world.icebear03.splendidenchants.enchant.mechanism.entry.operation

import org.bukkit.Material
import org.bukkit.block.data.Ageable
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.platform.util.takeItem
import world.icebear03.splendidenchants.api.PlayerAPI

object Plant {

    //FIXME 也许还有漏掉的种子type
    private val seedsMap = mapOf(
        Material.BEETROOT_SEEDS to Material.BEETROOTS,
        Material.MELON_SEEDS to Material.MELON_STEM,
        Material.PUMPKIN_SEEDS to Material.PUMPKIN_STEM,
        Material.TORCHFLOWER_SEEDS to Material.TORCHFLOWER_CROP,
        Material.WHEAT_SEEDS to Material.WHEAT,
        Material.CARROT to Material.CARROTS,
        Material.POTATO to Material.POTATOES
    )

    private fun getSeed(player: Player, seeds: String?): Material? {
        if (seeds == null)
            return null
        if (seeds == "ALL") {
            player.inventory.contents!!.forEach {
                if (it != null) {
                    if (seedsMap.containsKey(it.type)) {
                        return it.type
                    }
                }
            }
        }
        try {
            val type = Material.valueOf(seeds)
            if (player.inventory.containsAtLeast(ItemStack(type), 1)) {
                return type
            }
        } catch (ignored: Exception) {

        }
        return null
    }

    fun plant(player: Player, range: Int, seeds: String?) {
        if (range <= 1)
            return

        val block = PlayerAPI.lookingAtBlock(player, 6.0) ?: return
        val location = block.location
        for (x in -range + 1 until range) {
            for (z in -range + 1 until range) {
                val current = location.clone().add(x.toDouble(), 0.0, z.toDouble()).toHighestLocation()

                if (current.block.type != Material.FARMLAND)
                    continue
                val seedsType = getSeed(player, seeds) ?: return
                val newlyPlantedBlock = current.clone().add(0.0, 1.0, 0.0).block
                if (newlyPlantedBlock.type != Material.AIR)
                    continue

                newlyPlantedBlock.setType(seedsMap[seedsType]!!, true)
                (newlyPlantedBlock.blockData as Ageable).age = 0
                player.inventory.takeItem(1) {
                    it.type == seedsType
                }
            }
        }
    }
}