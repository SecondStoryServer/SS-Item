package me.syari.ss.item.equip

import me.syari.ss.item.custom.CustomItem
import me.syari.ss.item.custom.register.ItemRegister

interface EquipItem: CustomItem {
    companion object: ItemRegister<EquipItem>()

    data class Data(
        val equipItem: EquipItem,
        val enhance: Int,
        val enhancePlus: Int
    ){
        val itemStack
            get() = equipItem.itemStack
    }
}