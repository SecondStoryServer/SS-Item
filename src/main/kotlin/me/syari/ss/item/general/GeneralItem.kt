package me.syari.ss.item.general

import me.syari.ss.item.custom.CustomItem
import me.syari.ss.item.custom.register.ItemRegister

interface GeneralItem: CustomItem {
    override fun register() {
        register(id, this)
    }

    companion object: ItemRegister<GeneralItem>()
}