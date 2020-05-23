package me.syari.ss.item.general

import me.syari.ss.item.custom.CustomItem
import me.syari.ss.item.custom.register.ItemRegister

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