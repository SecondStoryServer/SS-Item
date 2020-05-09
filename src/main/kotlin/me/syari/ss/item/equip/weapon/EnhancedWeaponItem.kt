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
    private val damage = data.damage * enhanceRate
    private val criticalChance = data.criticalChance
    override val statusChange = mapOf(
        StatusType.Attack(data.damageElementType) to ("+$damage" to damage),
        StatusType.CriticalChance to ("+${criticalChance * 100}%" to criticalChance)
    )

    fun getAttackStatus(player: Player): PlayerStatus {
        val playerStatus = player.status.clone()
        statusChange.forEach { (statusType, value) ->
            playerStatus.add(
                StatusChange.Cause.Equipment, statusType, value.second, StatusChange.Type.Add
            )
        }
        playerStatus.damageElementType = data.damageElementType
        return playerStatus
    }
}