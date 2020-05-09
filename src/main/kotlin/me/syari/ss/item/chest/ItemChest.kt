package me.syari.ss.item.chest

import me.syari.ss.core.player.UUIDPlayer
import me.syari.ss.item.DatabaseConnector
import me.syari.ss.item.compass.CompassItem
import me.syari.ss.item.compass.CompassItem.Companion.allCompass
import me.syari.ss.item.equip.EnhancedEquipItem
import me.syari.ss.item.equip.EquipItem
import me.syari.ss.item.general.GeneralItem
import org.bukkit.inventory.ItemStack

interface ItemChest {
    val uuidPlayer: UUIDPlayer
    val sizeColumnName: String?
    val defaultMaxPage: Int?

    var maxPage: Int?
        get() = DatabaseConnector.Chest.MaxPage.get(uuidPlayer, this) ?: defaultMaxPage
        set(value) {
            DatabaseConnector.Chest.MaxPage.set(uuidPlayer, this, value)
        }

    data class General(override val uuidPlayer: UUIDPlayer): ItemChest {
        override val sizeColumnName = "General"
        override val defaultMaxPage = 2
        private val itemList = DatabaseConnector.Chest.General.get(uuidPlayer).toMutableMap()

        private var itemStackListCache: List<ItemStack>? = null
        private val itemStackList: List<ItemStack>
            get() {
                val lastItemStackList = itemStackListCache
                return if (lastItemStackList == null) {
                    val itemStackList = itemList.flatMap {
                        it.key.itemStack.clone { amount = it.value }.toItemStack
                    }
                    itemStackListCache = itemStackList
                    itemStackList
                } else {
                    lastItemStackList
                }
            }

        fun add(item: GeneralItem, amount: Int) {
            val totalAmount = itemList.getOrDefault(item, 0) + amount
            set(item, totalAmount)
        }

        fun remove(item: GeneralItem, amount: Int) {
            val totalAmount = itemList.getOrDefault(item, 0) - amount
            set(item, totalAmount)
        }

        fun set(item: GeneralItem, amount: Int) {
            if (0 < amount) {
                itemList[item] = amount
            } else {
                itemList.remove(item)
            }
            DatabaseConnector.Chest.General.set(uuidPlayer, item, amount)
            itemStackListCache = null
        }

        fun getList(page: Int): List<ItemStack>? {
            return itemStackList.slice(page, maxPage)
        }
    }

    data class Equip(override val uuidPlayer: UUIDPlayer): ItemChest {
        override val sizeColumnName = "Equip"
        override val defaultMaxPage = 2
        private val itemList = DatabaseConnector.Chest.Equip.get(uuidPlayer).toMutableList()

        fun add(item: EnhancedEquipItem) {
            itemList.add(item)
            DatabaseConnector.Chest.Equip.add(uuidPlayer, item)
        }

        fun remove(item: EnhancedEquipItem) {
            itemList.remove(item)
            DatabaseConnector.Chest.Equip.remove(uuidPlayer, item)
        }

        fun getList(page: Int): List<EnhancedEquipItem>? {
            return itemList.slice(page, maxPage)
        }
    }

    data class Compass(override val uuidPlayer: UUIDPlayer): ItemChest {
        override val sizeColumnName: String? = null
        override val defaultMaxPage: Int? = null
        private val itemList = DatabaseConnector.Chest.Compass.get(uuidPlayer).toMutableSet()

        fun has(item: CompassItem): Boolean {
            return itemList.contains(item)
        }

        fun add(item: CompassItem): Boolean {
            if (has(item)) return false
            itemList.add(item)
            DatabaseConnector.Chest.Compass.add(uuidPlayer, item)
            return true
        }

        fun remove(item: CompassItem): Boolean {
            if (!has(item)) return false
            itemList.remove(item)
            DatabaseConnector.Chest.Compass.remove(uuidPlayer, item)
            return true
        }

        fun getList(page: Int): Map<CompassItem, Boolean>? {
            return allCompass.slice(page, maxPage)?.associate { it to has(it) }
        }
    }

    private companion object {
        private fun <T> List<T>.slice(page: Int, maxPage: Int?): List<T>? {
            return when {
                page < 1 -> slice(1, maxPage)
                maxPage != null && maxPage < page -> null
                else -> slice(((page - 1) * 27) until (page * 27))
            }
        }
    }
}