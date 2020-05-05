package me.syari.ss.item.general.potion

import me.syari.ss.battle.status.player.PlayerStatus.Companion.status
import me.syari.ss.core.item.CustomItemStack
import me.syari.ss.item.general.GeneralItem
import me.syari.ss.item.general.GeneralItem.ItemType
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.PotionMeta

data class HealPotion(val size: Size): GeneralItem {
    override val id = "potion-heal-${size.id}"
    override val material = Material.POTION
    override val display = "&6回復ポーション &b〈${size.display}〉"
    override val description = "体力を ${size.healPercent * 100}% 回復する"
    override val itemType = ItemType.Potion
    override val itemStack: CustomItemStack
        get() = super.itemStack.apply {
            editMeta {
                if(this is PotionMeta){
                    color = Color.RED
                }
            }
        }

    fun use(player: Player){
        val playerStatus = player.status
        val maxHealth = playerStatus.maxHealth
        playerStatus.health += maxHealth * size.healPercent
    }

    enum class Size(val id: String, val display: String, val healPercent: Double, val maxAmount: Int) {
        Large("large", "大", 0.75, 5),
        Medium("medium", "中", 0.50, 10),
        Small("small", "小", 0.25, 15);

        val item = HealPotion(this)
    }

    companion object {
        fun register(){
            Size.values().forEach { size ->
                GeneralItem.register(size.id, size.item)
            }
        }
    }
}