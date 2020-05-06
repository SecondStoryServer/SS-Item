package me.syari.ss.item.custom

import org.bukkit.ChatColor

enum class ItemType(val color: ChatColor, val display: String) {
    Potion(ChatColor.LIGHT_PURPLE, "ポーション");

    override fun toString() = "$color$display"
}