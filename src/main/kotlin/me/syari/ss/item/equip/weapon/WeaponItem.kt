package me.syari.ss.item.equip.weapon

import me.syari.ss.battle.equipment.ElementType
import me.syari.ss.core.item.CustomItemStack
import me.syari.ss.item.equip.EnhancedEquipItem.Companion.getEnhance
import me.syari.ss.item.equip.EquipItem
import me.syari.ss.item.equip.weapon.indirect.BowItem
import me.syari.ss.item.equip.weapon.indirect.HarpItem
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
        fun create(
            weaponType: WeaponType,
            id: String,
            material: Material,
            display: String,
            description: String,
            elementType: ElementType,
            damage: Float,
            criticalChance: Float,
            attackSpeed: Float
        ): WeaponItem? {
            return when (weaponType) {
                WeaponType.Bow -> {
                    BowItem(
                        id, material, display, description, elementType, damage, criticalChance
                    )
                }
                WeaponType.Wand -> {
                    WandItem(
                        id, material, display, description, elementType, damage, criticalChance, attackSpeed
                    )
                }
                WeaponType.Harp -> {
                    HarpItem(
                        id, material, display, description, elementType, damage, criticalChance, attackSpeed
                    )
                }
                WeaponType.Sword, WeaponType.Axe, WeaponType.Knife, WeaponType.Mace, WeaponType.Knuckle -> {
                    MeleeItem(
                        id, material, display, description, elementType, damage, criticalChance, attackSpeed, weaponType
                    )
                }
            }
        }
    }
}