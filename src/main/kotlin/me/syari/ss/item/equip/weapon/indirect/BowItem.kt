package me.syari.ss.item.equip.weapon.indirect

import me.syari.ss.item.custom.ItemType
import me.syari.ss.item.equip.weapon.WeaponItem
import me.syari.ss.item.equip.weapon.WeaponType

class BowItem(
    data: Data
): WeaponItem(
    data, ItemType.Weapon(WeaponType.Bow)
)