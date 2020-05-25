package me.syari.ss.item.general.potion

import me.syari.ss.battle.status.player.PlayerStatus.Companion.status
import me.syari.ss.core.item.CustomItemStack
import me.syari.ss.item.ItemRarity
import me.syari.ss.item.custom.ClickableItem
import me.syari.ss.item.custom.ItemType
import me.syari.ss.item.custom.register.RegisterFunction
import me.syari.ss.item.general.GeneralItem
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.meta.PotionMeta

data class HealPotion(val size: Size): GeneralItem, ClickableItem {
    override val id = "potion-heal-${size.id}"
    override val material = Material.POTION
    override val display = "&6回復ポーション &b${size.display}"
    override val description = "体力を ${String.format("%.0f", size.healPercent * 100)}% 回復する"
    override val sortNumber = 0
    override val rarity: ItemRarity? = null
    override val itemType = ItemType.Potion
    override val itemStack: CustomItemStack
        get() = super.itemStack.apply {
            customModelData = size.customModelData
            editMeta {
                if (this is PotionMeta) {
                    color = Color.RED
                }
                addItemFlags(ItemFlag.HIDE_POTION_EFFECTS)
            }
        }

    override val coolDownTime = 5 * 20L
    override val coolDownType = ClickableItem.CoolDownType.Potion

    override fun onClick(player: Player, item: CustomItemStack, clickType: ClickableItem.ClickType): Boolean {
        if (clickType.isRight) {
            val playerStatus = player.status
            val maxHealth = playerStatus.maxHealth
            playerStatus.health += maxHealth * size.healPercent
            return true
        }
        return false
    }

    enum class Size(
        val id: String, val display: String, val customModelData: Int, val healPercent: Double, val maxAmount: Int
    ) {
        Large("large", "大", 2, 0.75, 5),
        Medium("medium", "中", 0, 0.50, 10),
        Small("small", "小", 1, 0.25, 15);

        val item = HealPotion(this)
    }

    companion object: RegisterFunction {
        override fun register() {
            Size.values().forEach { size ->
                GeneralItem.register(size.id, size.item)
            }
        }
    }
}