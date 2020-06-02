package me.syari.ss.item.equip

import me.syari.ss.core.item.CustomItemStack
import me.syari.ss.item.custom.CustomItem
import me.syari.ss.item.custom.register.ItemRegister
import me.syari.ss.item.equip.EnhancedEquipItem.Companion.getEnhance

interface EquipItem: CustomItem, Comparable<EquipItem> {
    val sortNumber: Int

    fun getEnhanced(item: CustomItemStack): EnhancedEquipItem {
        return EnhancedEquipItem(this, null, getEnhance(item))
    }

    override fun register() {
        register(id, this)
    }

    override fun compareTo(other: EquipItem): Int {
        return sortNumber.compareTo(other.sortNumber)
    }

    companion object: ItemRegister<EquipItem>()
}