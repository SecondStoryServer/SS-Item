package me.syari.ss.item.custom

import org.bukkit.entity.Player

interface ClickableItem {
    fun onClick(player: Player, clickType: Type)

    enum class Type {
        Right, Left;

        val isRight get() = this == Right
        val isLeft get() = this == Left
    }
}