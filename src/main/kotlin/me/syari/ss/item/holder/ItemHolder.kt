package me.syari.ss.item.holder

import me.syari.ss.core.player.UUIDPlayer
import me.syari.ss.item.itemRegister.custom.CustomItem
import me.syari.ss.item.itemRegister.equip.armor.ArmorItem
import me.syari.ss.item.itemRegister.equip.weapon.WeaponItem

data class ItemHolder(val uuidPlayer: UUIDPlayer) {
    private val itemList = mutableMapOf<Int, CustomItem>()

    fun getHotBarItem(index: Int): CustomItem? {
        return if (index in 0..8) itemList[index] else throw IndexOutOfBoundsException("Index: $index, Range: 0..8")
    }

    fun setHotBarItem(index: Int, item: CustomItem?) {
        if (index !in 0..8) throw IndexOutOfBoundsException("Index: $index, Range: 0..8")
        if (item != null) {
            itemList[index] = item
        } else {
            itemList.remove(index)
        }
    }

    var offHandItem: CustomItem?
        get() = itemList[OFFHAND_SLOT]
        set(value) {
            if (value != null) {
                itemList[OFFHAND_SLOT] = value
            } else {
                itemList.remove(OFFHAND_SLOT)
            }
        }

    var extraWeaponItem = listOf<WeaponItem>()

    private val armorList = mutableMapOf<ArmorSlot, ArmorItem>()

    fun getArmorItem(armorSlot: ArmorSlot): ArmorItem? {
        return armorList[armorSlot]
    }

    fun setArmorItem(armorSlot: ArmorSlot, item: ArmorItem?) {
        if (item != null) {
            armorList[armorSlot] = item
        } else {
            armorList.remove(armorSlot)
        }
    }

    enum class ArmorSlot(val slot: Int) {
        BOOTS(36),
        LEGGINGS(37),
        CHESTPLATE(38),
        HELMET(39);
    }

    companion object {
        private const val OFFHAND_SLOT = 40
    }
}