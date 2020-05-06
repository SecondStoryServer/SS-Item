package me.syari.ss.item

import me.syari.ss.core.auto.Event
import me.syari.ss.core.item.CustomItemStack
import me.syari.ss.item.custom.ClickableItem
import me.syari.ss.item.custom.CustomItem
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

object EventListener: Event {
    @EventHandler
    fun on(e: PlayerInteractEvent){
        val item = e.item?.let {
            CustomItemStack.create(it)
        } ?: return
        val customItem = CustomItem.from(item) ?: return
        if(customItem is ClickableItem){
            val player = e.player
            val clickType = when(e.action){
                Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK -> ClickableItem.Type.Left
                Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK -> ClickableItem.Type.Right
                else -> return
            }
            customItem.onClick(player, clickType)
        }
    }
}