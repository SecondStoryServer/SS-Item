package me.syari.ss.item.equip.weapon

import me.syari.ss.battle.equipment.ElementType
import me.syari.ss.core.item.CustomItemStack
import me.syari.ss.item.equip.EnhancedEquipItem.Companion.getEnhance
import me.syari.ss.item.equip.EquipItem
import me.syari.ss.item.equip.weapon.indirect.BowItem
import me.syari.ss.item.equip.weapon.indirect.WandItem
import me.syari.ss.item.equip.weapon.melee.MeleeItem
import org.bukkit.Material

interface WeaponItem: EquipItem {
    val damageElementType: ElementType
    val damage: Float
    val criticalChance: Float
    val attackSpeed: Float

    override fun getEnhanced(item: CustomItemStack): EnhancedWeaponItem {
        return EnhancedWeaponItem(this, getEnhance(item))
    }

    companion object {
        const val projectileShooterStatusMetaDataKey = "ss-item-projectile-shooter-status"
        const val arrowForceMetaDataKey = "ss-item-arrow-force"

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