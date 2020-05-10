package me.syari.ss.item.equip

import me.syari.ss.core.item.CustomItemStack
import me.syari.ss.item.custom.CustomItem
import me.syari.ss.item.custom.register.ItemRegister
import me.syari.ss.item.equip.EnhancedEquipItem.Companion.getEnhance

interface EquipItem: CustomItem {
    fun getEnhanced(item: CustomItemStack): EnhancedEquipItem {
        return EnhancedEquipItem(this, getEnhance(item))
    }

    companion object: ItemRegister<EquipItem>()
}