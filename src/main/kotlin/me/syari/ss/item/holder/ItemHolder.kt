package me.syari.ss.item.holder

import me.syari.ss.core.player.UUIDPlayer
import me.syari.ss.item.DatabaseConnector
import me.syari.ss.item.itemRegister.custom.CustomItem
import me.syari.ss.item.itemRegister.equip.armor.EnhancedArmorItem
import me.syari.ss.item.itemRegister.equip.weapon.EnhancedWeaponItem
import org.bukkit.OfflinePlayer

data class ItemHolder(private val uuidPlayer: UUIDPlayer) {
    private val itemList = mutableMapOf<Int, CustomItem>()

    val allNormalItem
        get() = itemList.toMap()

    internal fun setNormalItem(index: Int, item: CustomItem?) {
        if (item != null) {
            itemList[index] = item
        } else {
            itemList.remove(index)
        }
    }

    fun getHotBarItem(index: Int): CustomItem? {
        return if (index in 0..8) itemList[index] else throw IndexOutOfBoundsException("Index: $index, Range: 0..8")
    }

    fun setHotBarItem(index: Int, item: CustomItem?) {
        if (index !in 0..8) throw IndexOutOfBoundsException("Index: $index, Range: 0..8")
        setNormalItem(index, item)
    }

    var offHandItem: CustomItem?
        get() = itemList[OFFHAND_SLOT]
        set(value) {
            setNormalItem(OFFHAND_SLOT, value)
        }

    var extraWeaponItem = listOf<EnhancedWeaponItem>()

    private val armorList = mutableMapOf<ArmorSlot, EnhancedArmorItem>()

    val allArmorItem
        get() = armorList.toMap()

    fun getArmorItem(armorSlot: ArmorSlot): EnhancedArmorItem? {
        return armorList[armorSlot]
    }

    fun setArmorItem(armorSlot: ArmorSlot, item: EnhancedArmorItem?) {
        if (item != null) {
            armorList[armorSlot] = item
        } else {
            armorList.remove(armorSlot)
        }
    }

    enum class ArmorSlot(val slot: Int, val vanillaSlot: Int) {
        BOOTS(3, 36),
        LEGGINGS(2, 37),
        CHESTPLATE(1, 38),
        HELMET(0, 39);

        companion object {
            fun getBySlot(index: Int): ArmorSlot? {
                return values().firstOrNull { it.slot == index }
            }
        }
    }

    enum class Type {
        Normal,
        Armor,
        ExtraWeapon
    }

    companion object {
        internal const val OFFHAND_SLOT = 40

        private val itemHolderMap = mutableMapOf<UUIDPlayer, ItemHolder>()

        val OfflinePlayer.itemHolder
            get() = UUIDPlayer(this).itemHolder

        private val UUIDPlayer.itemHolder
            get() = itemHolderMap.getOrPut(this) { DatabaseConnector.Holder.get(this) }
    }
}