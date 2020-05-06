package me.syari.ss.item.equip

import me.syari.ss.item.custom.CustomItem
import me.syari.ss.item.custom.register.RegisterList

interface EquipItem: CustomItem {
    companion object: RegisterList<EquipItem>()
}