package me.syari.ss.item.equip.weapon

import me.syari.ss.battle.damage.DamageCalculator
import me.syari.ss.battle.status.EntityStatus
import me.syari.ss.battle.status.player.PlayerStatus
import me.syari.ss.battle.status.player.StatusChange
import me.syari.ss.battle.status.player.StatusType
import me.syari.ss.item.custom.CustomItem

interface WeaponItem: CustomItem {
    val onAttackStatus: Map<StatusType, Float>

    fun getDamage(playerStatus: PlayerStatus, victimStatus: EntityStatus): Float {
        onAttackStatus.forEach { (statusType, value) ->
            playerStatus.add(
                StatusChange.Cause.Equipment, statusType, value, StatusChange.Type.Add
            )
        }
        return DamageCalculator.getDamage(playerStatus, victimStatus)
    }
}