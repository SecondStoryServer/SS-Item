package me.syari.ss.item.itemRegister.custom

import me.syari.ss.item.itemRegister.equip.weapon.WeaponType
import org.bukkit.ChatColor

sealed class ItemType(val color: ChatColor, val display: String) {
    override fun toString() = "$color$display"

    object Compass: ItemType(ChatColor.GOLD, "コンパス")
    object Potion: ItemType(ChatColor.LIGHT_PURPLE, "ポーション")
    data class Weapon(val weaponType: WeaponType): ItemType(ChatColor.DARK_GREEN, "武具 ${weaponType.display}")
    object Armor: ItemType(ChatColor.DARK_BLUE, "防具")
}