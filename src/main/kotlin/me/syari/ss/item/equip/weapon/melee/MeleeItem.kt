package me.syari.ss.item.equip.weapon.melee

import me.syari.ss.item.custom.ItemType
import me.syari.ss.item.equip.weapon.WeaponItem
import me.syari.ss.item.equip.weapon.WeaponType

class MeleeItem(
    data: Data, meleeType: Type
): WeaponItem(
    data, ItemType.Weapon(meleeType.toWeaponType)
) {
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