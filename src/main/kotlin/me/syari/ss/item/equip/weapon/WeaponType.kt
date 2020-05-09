package me.syari.ss.item.equip.weapon

import me.syari.ss.item.equip.weapon.melee.MeleeItem

enum class WeaponType(
    val id: String, val display: String
) {
    Sword(
        "sword", "剣"
    ),
    Axe(
        "axe", "斧"
    ),
    Bow(
        "bow", "弓"
    ),
    Knife(
        "knife", "短剣"
    ),
    Wand(
        "wand", "杖"
    ),
    Mace(
        "mace", "棍"
    ),
    Knuckle(
        "knuckle", "拳"
    );

    inline val isBowItem
        get() = this == Bow

    inline val isWandItem
        get() = this == Wand

    inline val isMeleeItem
        get() = MeleeItem.Type.isMeleeItem(this)

    inline val toMeleeItemType
        get() = MeleeItem.Type.fromWeaponType(this)

    companion object {
        fun getById(id: String): WeaponType? {
            return values().firstOrNull { it.id == id }
        }
    }
}