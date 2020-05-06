package me.syari.ss.item.equip.weapon.indirect

import me.syari.ss.item.custom.ItemType
import me.syari.ss.item.equip.weapon.WeaponItem
import me.syari.ss.item.equip.weapon.WeaponType
import org.bukkit.Material

open class BowItem(
    override val id: String,
    override val material: Material,
    override val display: String,
    override val description: String
): WeaponItem {
    override val itemType = ItemType.Weapon(WeaponType.Bow)
}