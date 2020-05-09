package me.syari.ss.item.custom

import me.syari.ss.core.item.CustomItemStack
import org.bukkit.entity.Player

interface ClickableItem {
    fun onClick(player: Player, item: CustomItemStack, clickType: Type)

    enum class Type {
        Right,
        Left;

        val isRight get() = this == Right
        val isLeft get() = this == Left
    }
}