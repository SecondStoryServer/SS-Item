package me.syari.ss.item.custom

import me.syari.ss.core.item.CustomItemStack
import org.bukkit.entity.Player

interface ClickableItem {
    val coolDownTime: Long
    val coolDownType: CoolDownType

    fun onClick(player: Player, item: CustomItemStack, clickType: ClickType): Boolean

    enum class ClickType {
        Right,
        Left;

        val isRight get() = this == Right
        val isLeft get() = this == Left
    }

    enum class CoolDownType {
        Weapon,
        Potion
    }
}