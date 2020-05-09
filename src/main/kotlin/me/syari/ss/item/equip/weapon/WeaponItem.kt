package me.syari.ss.item.equip.weapon

import me.syari.ss.battle.damage.DamageCalculator
import me.syari.ss.battle.equipment.ElementType
import me.syari.ss.battle.status.EntityStatus
import me.syari.ss.battle.status.player.PlayerStatus
import me.syari.ss.battle.status.player.StatusChange
import me.syari.ss.battle.status.player.StatusType
import me.syari.ss.item.custom.CustomItem

interface WeaponItem: CustomItem {
    val damageElementType: ElementType
    val damage: Float
    val criticalChange: Float
    val attackSpeed: Float

    fun getDamage(playerStatus: PlayerStatus, victimStatus: EntityStatus): Float {
        val onAttackStatus = mapOf(
            StatusType.Attack(damageElementType) to damage,
            StatusType.CriticalChance to criticalChange
        )
        onAttackStatus.forEach { (statusType, value) ->
            playerStatus.add(
                StatusChange.Cause.Equipment, statusType, value, StatusChange.Type.Add
            )
        }
        playerStatus.damageElementType = damageElementType
        return DamageCalculator.getDamage(playerStatus, victimStatus)
    }
}