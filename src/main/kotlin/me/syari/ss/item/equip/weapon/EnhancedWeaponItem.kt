package me.syari.ss.item.equip.weapon

import me.syari.ss.battle.status.player.PlayerStatus
import me.syari.ss.battle.status.player.PlayerStatus.Companion.status
import me.syari.ss.battle.status.player.StatusChange
import me.syari.ss.battle.status.player.StatusType
import me.syari.ss.item.equip.EnhancedEquipItem
import org.bukkit.entity.Player

class EnhancedWeaponItem(
    override val data: WeaponItem, enhance: Int, enhancePlus: Int
): EnhancedEquipItem(
    data, enhance, enhancePlus
) {
    fun getAttackStatus(player: Player): PlayerStatus {
        val playerStatus = player.status.clone()

        fun addStatusChange(statusType: StatusType, value: Float) {
            playerStatus.add(
                StatusChange.Cause.Equipment, statusType, value, StatusChange.Type.Add
            )
        }

        addStatusChange(StatusType.Attack(data.damageElementType), data.damage * enhanceRate)
        addStatusChange(StatusType.CriticalChance, data.criticalChance)
        playerStatus.damageElementType = data.damageElementType
        return playerStatus
    }
}