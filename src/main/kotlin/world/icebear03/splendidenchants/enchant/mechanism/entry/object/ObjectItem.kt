package world.icebear03.splendidenchants.enchant.mechanism.entry.`object`

import com.mcstarrysky.starrysky.function.deserializeItemStackFromBase64
import com.mcstarrysky.starrysky.function.serializeToBase64
import org.bukkit.inventory.ItemStack
import world.icebear03.splendidenchants.api.calcToInt
import world.icebear03.splendidenchants.api.name
import world.icebear03.splendidenchants.enchant.mechanism.entry.internal.ObjectEntry
import world.icebear03.splendidenchants.enchant.mechanism.entry.internal.objLivingEntity

object ObjectItem : ObjectEntry<ItemStack>() {

    override fun modify(
        obj: ItemStack,
        cmd: String,
        params: List<String>
    ): Boolean {
        when (cmd) {
            "修改名称" -> obj.name = params[0]
            "损耗耐久" -> obj.damage(params[0].calcToInt(), objLivingEntity.d(params[1])!!)
        }
        return true
    }

    override fun holderize(obj: ItemStack) = this to "物品=${obj.serializeToBase64()}"

    override fun disholderize(holder: String) = holder.replace("物品=", "").deserializeItemStackFromBase64()
}