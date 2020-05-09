package me.syari.ss.item.equip.weapon

import me.syari.ss.battle.damage.DamageCalculator
import me.syari.ss.battle.equipment.ElementType
import me.syari.ss.battle.status.EntityStatus
import me.syari.ss.battle.status.player.PlayerStatus
import me.syari.ss.battle.status.player.StatusChange
import me.syari.ss.battle.status.player.StatusType
import me.syari.ss.item.custom.CustomItem
import me.syari.ss.item.equip.weapon.indirect.BowItem
import me.syari.ss.item.equip.weapon.indirect.WandItem
import me.syari.ss.item.equip.weapon.melee.MeleeItem
import org.bukkit.Material

interface WeaponItem: CustomItem {
    val damageElementType: ElementType
    val damage: Float
    val criticalChance: Float
    val attackSpeed: Float

    fun getDamage(playerStatus: PlayerStatus, victimStatus: EntityStatus): Float {
        val onAttackStatus = mapOf(
            StatusType.Attack(damageElementType) to damage, StatusType.CriticalChance to criticalChance
        )
        onAttackStatus.forEach { (statusType, value) ->
            playerStatus.add(
                StatusChange.Cause.Equipment, statusType, value, StatusChange.Type.Add
            )
        }
        playerStatus.damageElementType = damageElementType
        return DamageCalculator.getDamage(playerStatus, victimStatus)
    }

    companion object {
        fun create(
            weaponType: WeaponType,
            id: String,
            material: Material,
            display: String,
            description: String,
            damageElementType: ElementType,
            damage: Float,
            criticalChance: Float,
            attackSpeed: Float
        ): WeaponItem? {
            return when {
                weaponType.isBowItem -> {
                    BowItem(
                        id, material, display, description, damageElementType, damage, criticalChance, attackSpeed
                    )
                }
                weaponType.isWandItem -> {
                    WandItem(
                        id, material, display, description, damageElementType, damage, criticalChance, attackSpeed
                    )
                }
                weaponType.isMeleeItem -> weaponType.toMeleeItemType?.let {
                    MeleeItem(
                        id, material, display, description, damageElementType, damage, criticalChance, attackSpeed, it
                    )
                }
                else -> null
            }
        }
    }
}