package me.syari.ss.item.equip.weapon.melee

import me.syari.ss.battle.equipment.ElementType
import me.syari.ss.item.custom.ItemType
import me.syari.ss.item.equip.weapon.WeaponItem
import me.syari.ss.item.equip.weapon.WeaponType
import org.bukkit.Material

data class MeleeItem(
    override val id: String,
    override val material: Material,
    override val display: String,
    override val description: String,
    override val damageElementType: ElementType,
    override val damage: Float,
    override val criticalChange: Float,
    override val attackSpeed: Float,
    val meleeType: Type
): WeaponItem {
    override val itemType = ItemType.Weapon(meleeType.toWeaponType)

    enum class Type(val toWeaponType: WeaponType) {
        Sword(WeaponType.Sword),
        Axe(WeaponType.Axe),
        Knife(WeaponType.Knife),
        Mace(WeaponType.Mace),
        Knuckle(WeaponType.Knuckle)
    }
}