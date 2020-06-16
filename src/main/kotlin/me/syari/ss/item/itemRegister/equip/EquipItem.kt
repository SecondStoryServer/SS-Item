package me.syari.ss.item.itemRegister.equip

import me.syari.ss.core.item.CustomItemStack
import me.syari.ss.item.itemRegister.custom.CustomItem
import me.syari.ss.item.itemRegister.custom.register.ItemRegister
import java.util.UUID

interface EquipItem: CustomItem, Comparable<EquipItem> {
    val sortNumber: Int

    fun getEnhanced(item: CustomItemStack): EnhancedEquipItem

    fun getEnhanced(
        uuid: UUID?,
        enhance: Int
    ): EnhancedEquipItem

    override fun register() {
        register(id, this)
    }

    override fun compareTo(other: EquipItem): Int {
        return sortNumber.compareTo(other.sortNumber)
    }

    companion object: ItemRegister<EquipItem>()
}