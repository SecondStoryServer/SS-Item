package me.syari.ss.item.general.potion

import me.syari.ss.battle.status.player.PlayerStatus.Companion.status
import me.syari.ss.item.general.GeneralItem
import org.bukkit.entity.Player

data class HealPotion(val size: Size): GeneralItem {
    fun use(player: Player){
        val playerStatus = player.status
        val maxHealth = playerStatus.maxHealth
        playerStatus.health += maxHealth * size.healPercent
    }

    enum class Size(val id: String, val display: String, val healPercent: Double, val maxAmount: Int) {
        Large("large", "大", 0.75, 5),
        Medium("medium", "中", 0.50, 10),
        Small("small", "小", 0.25, 15);

        companion object {
            fun getById(id: String): Size? {
                return values().firstOrNull { it.id == id }
            }
        }
    }
}