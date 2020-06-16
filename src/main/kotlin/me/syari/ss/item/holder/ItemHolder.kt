package me.syari.ss.item.holder

import me.syari.ss.core.player.UUIDPlayer
import me.syari.ss.item.DatabaseConnector
import me.syari.ss.item.itemRegister.custom.CustomItem
import me.syari.ss.item.itemRegister.equip.armor.ArmorItem
import me.syari.ss.item.itemRegister.equip.armor.EnhancedArmorItem
import me.syari.ss.item.itemRegister.equip.weapon.EnhancedWeaponItem
import org.bukkit.OfflinePlayer

data class ItemHolder(private val uuidPlayer: UUIDPlayer) {
    private val itemList = mutableMapOf<Int, CustomItem>()

    val allNormalItem
        get() = itemList.toMap()

    internal fun setNormalItem(
        index: Int,
        item: CustomItem?
    ) {
        if (item != null) {
            itemList[index] = item
        } else {
            itemList.remove(index)
        }
    }

    fun getHotBarItem(index: Int): CustomItem? {
        return if (index in 0..8) itemList[index] else throw IndexOutOfBoundsException("Index: $index, Range: 0..8")
    }

    fun setHotBarItem(
        index: Int,
        item: CustomItem?
    ) {
        if (index !in 0..8) throw IndexOutOfBoundsException("Index: $index, Range: 0..8")
        setNormalItem(index, item)
    }

    var offHandItem: CustomItem?
        get() = itemList[OFFHAND_SLOT]
        set(value) {
            setNormalItem(OFFHAND_SLOT, value)
        }

    private val extraWeaponItem = mutableMapOf<Int, EnhancedWeaponItem>()

    val allExtraWeaponItem
        get() = extraWeaponItem.toMap()

    fun getExtraWeaponItem(index: Int): EnhancedWeaponItem? {
        return if (index in 0..3) extraWeaponItem[index] else throw IndexOutOfBoundsException("Index: $index, Range: 0..3")
    }

    fun setExtraWeaponItem(
        index: Int,
        item: EnhancedWeaponItem?
    ) {
        if (index !in 0..3) throw IndexOutOfBoundsException("Index: $index, Range: 0..3")
        if (item != null) {
            extraWeaponItem[index] = item
        } else {
            extraWeaponItem.remove(index)
        }
    }

    private val armorList = mutableMapOf<ArmorSlot, EnhancedArmorItem>()

    val allArmorItem
        get() = armorList.toMap()

    fun getArmorItem(armorSlot: ArmorSlot): EnhancedArmorItem? {
        return armorList[armorSlot]
    }

    fun setArmorItem(
        armorSlot: ArmorSlot,
        item: EnhancedArmorItem?
    ) {
        if (item != null) {
            armorList[armorSlot] = item
        } else {
            armorList.remove(armorSlot)
        }
    }

    enum class ArmorSlot(
        val slot: Int,
        val vanillaSlot: Int,
        val armorType: ArmorItem.ArmorType
    ) {
        BOOTS(3, 36, ArmorItem.ArmorType.BOOTS),
        LEGGINGS(2, 37, ArmorItem.ArmorType.LEGGINGS),
        CHESTPLATE(1, 38, ArmorItem.ArmorType.CHESTPLATE),
        HELMET(0, 39, ArmorItem.ArmorType.HELMET);

        fun isAvailable(armorItem: ArmorItem): Boolean {
            return armorType == armorItem.armorType
        }

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