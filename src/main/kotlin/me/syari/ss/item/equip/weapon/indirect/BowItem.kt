package me.syari.ss.item.equip.weapon.indirect

import me.syari.ss.battle.equipment.ElementType
import me.syari.ss.item.ItemRarity
import me.syari.ss.item.equip.weapon.WeaponItem
import me.syari.ss.item.equip.weapon.WeaponType
import org.bukkit.Material

class BowItem(
    override val id: String,
    override val material: Material,
    override val display: String,
    override val description: String,
    override val rarity: ItemRarity,
    override val damageElementType: ElementType,
    override val damage: Float,
    override val criticalChance: Float
): WeaponItem {
    override val weaponType = WeaponType.Bow
    override val attackSpeed = 1.0F
}