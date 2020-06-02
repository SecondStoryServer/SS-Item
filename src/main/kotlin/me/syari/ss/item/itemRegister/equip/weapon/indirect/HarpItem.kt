package me.syari.ss.item.itemRegister.equip.weapon.indirect

import me.syari.ss.battle.equipment.ElementType
import me.syari.ss.item.ItemRarity
import me.syari.ss.item.itemRegister.equip.weapon.WeaponItem
import me.syari.ss.item.itemRegister.equip.weapon.WeaponType
import org.bukkit.Material

class HarpItem(
    override val id: String,
    override val material: Material,
    override val display: String,
    override val description: String,
    override val rarity: ItemRarity,
    override val damageElementType: ElementType,
    override val damage: Float,
    override val criticalChance: Float,
    override val attackSpeed: Float
): WeaponItem {
    override val weaponType = WeaponType.Harp
}