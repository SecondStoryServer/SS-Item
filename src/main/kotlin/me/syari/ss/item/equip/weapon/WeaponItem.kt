package me.syari.ss.item.equip.weapon

import me.syari.ss.battle.damage.DamageCalculator
import me.syari.ss.battle.equipment.ElementType
import me.syari.ss.battle.status.EntityStatus
import me.syari.ss.battle.status.player.PlayerStatus
import me.syari.ss.battle.status.player.StatusChange
import me.syari.ss.battle.status.player.StatusType
import me.syari.ss.item.custom.CustomItem
import me.syari.ss.item.custom.ItemType
import me.syari.ss.item.equip.weapon.indirect.BowItem
import me.syari.ss.item.equip.weapon.indirect.WandItem
import me.syari.ss.item.equip.weapon.melee.MeleeItem
import org.bukkit.Material

open class WeaponItem(data: Data, override val itemType: ItemType): CustomItem {
    override val id = data.id
    override val material = data.material
    override val display = data.display
    override val description = data.description
    val damageElementType = data.damageElementType
    val damage: Float = data.damage
    val criticalChance = data.criticalChance
    val attackSpeed = data.attackSpeed

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

    data class Data(
        val id: String,
        val material: Material,
        val display: String,
        val description: String,
        val damageElementType: ElementType,
        val damage: Float,
        val criticalChance: Float,
        val attackSpeed: Float
    )

    companion object {
        fun create(
            weaponType: WeaponType, data: Data
        ): WeaponItem? {
            return when {
                weaponType.isBowItem -> BowItem(data)
                weaponType.isWandItem -> WandItem(data)
                weaponType.isMeleeItem -> weaponType.toMeleeItemType?.let { MeleeItem(data, it) }
                else -> null
            }
        }
    }
}