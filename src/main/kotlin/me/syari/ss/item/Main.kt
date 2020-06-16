package me.syari.ss.item

import me.syari.ss.core.auto.Event
import me.syari.ss.core.auto.OnEnable
import me.syari.ss.item.itemRegister.compass.CompassItem
import me.syari.ss.item.itemRegister.custom.register.ItemRegister
import me.syari.ss.item.itemRegister.custom.register.RegisterFunction
import me.syari.ss.item.itemRegister.equip.EquipItem
import me.syari.ss.item.itemRegister.general.GeneralItem
import me.syari.ss.item.itemRegister.general.potion.HealPotion
import org.bukkit.plugin.java.JavaPlugin

class Main: JavaPlugin() {
    companion object {
        internal lateinit var itemPlugin: JavaPlugin
    }

    override fun onEnable() {
        itemPlugin = this
        ItemRegister.add(GeneralItem, EquipItem, CompassItem)
        RegisterFunction.add(HealPotion)
        OnEnable.register(ConfigLoader, DatabaseConnector)
        Event.register(this, EventListener)
    }
}