package me.syari.ss.item.equip

import me.syari.ss.item.custom.CustomItem
import me.syari.ss.item.custom.register.ItemRegister

interface EquipItem: CustomItem {
    companion object: ItemRegister<EquipItem>()
}