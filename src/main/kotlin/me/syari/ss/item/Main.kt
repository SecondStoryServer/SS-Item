package me.syari.ss.item

import me.syari.ss.core.auto.Event
import me.syari.ss.core.auto.OnEnable
import me.syari.ss.item.custom.CustomItem
import me.syari.ss.item.custom.register.RegisterFunction
import me.syari.ss.item.custom.register.ItemRegister
import me.syari.ss.item.equip.EquipItem
import me.syari.ss.item.general.GeneralItem
import me.syari.ss.item.general.potion.HealPotion
import org.bukkit.plugin.java.JavaPlugin

class Main: JavaPlugin() {
    companion object {
        internal lateinit var itemPlugin: JavaPlugin
    }

    override fun onEnable() {
        itemPlugin = this
        RegisterFunction.add(
            HealPotion
        )
        ItemRegister.add(
            GeneralItem, EquipItem
        )
        OnEnable.register(
            ConfigLoader
        )
        Event.register(this,
            EventListener
        )
    }
}