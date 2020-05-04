package me.syari.ss.item.chest

import me.syari.ss.core.player.UUIDPlayer
import me.syari.ss.item.equip.EquipItem
import me.syari.ss.item.general.GeneralItem

interface ItemChest {
    val sizeColumnName: String
    val defaultMaxPage: Int

    var maxPage: Int
        get() = TODO("Not yet implemented")
        set(value) {
            TODO("Not yet implemented")
        }

    data class General(val uuidPlayer: UUIDPlayer): ItemChest {
        override val sizeColumnName = "general"
        override val defaultMaxPage = 2
        private val itemList = mutableListOf<GeneralItem>()

        init {
            // Load Item From SQL
        }

        fun add(vararg item: GeneralItem) {
            itemList.addAll(item)
            // Add Item To SQL
        }

        fun remove(vararg item: GeneralItem) {
            itemList.removeAll(item)
            // Remove Item From SQL
        }

        fun getList(page: Int): List<GeneralItem>? {
            return when {
                page < 1 -> getList(1)
                maxPage < page -> null
                else -> itemList.slice(((page - 1) * 27) until (page * 27))
            }
        }
    }

    data class Equip(val uuidPlayer: UUIDPlayer): ItemChest {
        override val sizeColumnName = "equip"
        override val defaultMaxPage = 2
        private val itemList = mutableListOf<EquipItem>()

        init {
            // Load Item From SQL
        }

        fun add(vararg item: EquipItem) {
            itemList.addAll(item)
            // Add Item To SQL
        }

        fun remove(vararg item: EquipItem) {
            itemList.removeAll(item)
            // Remove Item From SQL
        }

        fun getList(page: Int): List<EquipItem>? {
            return when {
                page < 1 -> getList(1)
                maxPage < page -> null
                else -> itemList.slice(((page - 1) * 27) until (page * 27))
            }
        }
    }
}