package me.syari.ss.item.equip.weapon

import me.syari.ss.battle.equipment.ElementType
import me.syari.ss.core.item.CustomItemStack
import me.syari.ss.item.ItemRarity
import me.syari.ss.item.custom.ItemType
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
    val weaponType: WeaponType
    override val itemType
        get() = ItemType.Weapon(weaponType)
    override val sortNumber
        get() = 1

    override fun getEnhanced(item: CustomItemStack): EnhancedWeaponItem {
        return EnhancedWeaponItem(this, null, getEnhance(item))
    }

    override fun compareTo(other: EquipItem): Int {
        return if (other is WeaponItem) {
            weaponType.compareTo(other.weaponType)
        } else {
            super.compareTo(other)
        }
    }

    companion object {
        fun create(
            weaponType: WeaponType,
            id: String,
            material: Material,
            display: String,
            description: String,
            rarity: ItemRarity,
            elementType: ElementType,
            damage: Float,
            criticalChance: Float,
            attackSpeed: Float
        ): WeaponItem {
            return when (weaponType) {
                WeaponType.Bow -> {
                    BowItem(
                        id, material, display, description, rarity, elementType, damage, criticalChance
                    )
                }
                WeaponType.Wand -> {
                    WandItem(
                        id, material, display, description, rarity, elementType, damage, criticalChance, attackSpeed
                    )
                }
                WeaponType.Harp -> {
                    HarpItem(
                        id, material, display, description, rarity, elementType, damage, criticalChance, attackSpeed
                    )
                }
                WeaponType.Sword, WeaponType.Axe, WeaponType.Knife, WeaponType.Mace, WeaponType.Knuckle -> {
                    MeleeItem(
                        id,
                        material,
                        display,
                        description,
                        rarity,
                        elementType,
                        damage,
                        criticalChance,
                        attackSpeed,
                        weaponType
                    )
                }
            }
        }
    }
}