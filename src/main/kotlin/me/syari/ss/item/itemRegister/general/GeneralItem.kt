package me.syari.ss.item.itemRegister.general

import me.syari.ss.item.itemRegister.custom.CustomItem
import me.syari.ss.item.itemRegister.custom.register.ItemRegister

interface GeneralItem: CustomItem, Comparable<GeneralItem> {
    val sortNumber: Int

    override fun compareTo(other: GeneralItem): Int {
        return sortNumber.compareTo(other.sortNumber)
    }

    override fun register() {
        register(id, this)
    }

    companion object: ItemRegister<GeneralItem>()
}