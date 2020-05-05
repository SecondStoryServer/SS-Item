package me.syari.ss.item

import me.syari.ss.core.auto.OnEnable
import me.syari.ss.item.general.GeneralItem
import org.bukkit.plugin.java.JavaPlugin

class Main: JavaPlugin() {
    companion object {
        internal lateinit var itemPlugin: JavaPlugin
    }

    override fun onEnable() {
        itemPlugin = this
        OnEnable.register(ConfigLoader, GeneralItem.Register)
    }
}