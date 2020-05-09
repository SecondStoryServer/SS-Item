package me.syari.ss.item.equip.weapon.indirect

import me.syari.ss.item.custom.ItemType
import me.syari.ss.item.equip.weapon.WeaponItem
import me.syari.ss.item.equip.weapon.WeaponType

class WandItem(
    data: Data
): WeaponItem(
    data, ItemType.Weapon(WeaponType.Wand)
)