package me.syari.ss.item.equip.weapon.melee

import me.syari.ss.battle.equipment.ElementType
import me.syari.ss.item.custom.ItemType
import me.syari.ss.item.equip.weapon.WeaponItem
import me.syari.ss.item.equip.weapon.WeaponType
import org.bukkit.Material

class MeleeItem(
    override val id: String,
    override val material: Material,
    override val display: String,
    override val description: String,
    override val damageElementType: ElementType,
    override val damage: Float,
    override val criticalChance: Float,
    override val attackSpeed: Float,
    meleeType: Type
): WeaponItem {
    override val itemType = ItemType.Weapon(meleeType.toWeaponType)

    enum class Type(val toWeaponType: WeaponType) {
        Sword(WeaponType.Sword),
        Axe(WeaponType.Axe),
        Knife(WeaponType.Knife),
        Mace(WeaponType.Mace),
        Knuckle(WeaponType.Knuckle);

        companion object {
            fun fromWeaponType(weaponType: WeaponType): Type? {
                return values().firstOrNull { it.toWeaponType == weaponType }
            }

            fun isMeleeItem(weaponType: WeaponType): Boolean {
                return values().map { it.toWeaponType }.contains(weaponType)
            }
        }
    }
}