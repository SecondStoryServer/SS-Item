package me.syari.ss.item.chest

import me.syari.ss.core.player.UUIDPlayer
import me.syari.ss.item.DatabaseConnector
import me.syari.ss.item.equip.EquipItem
import me.syari.ss.item.general.GeneralItem
import org.bukkit.inventory.ItemStack

interface ItemChest {
    val uuidPlayer: UUIDPlayer
    val sizeColumnName: String
    val defaultMaxPage: Int

    var maxPage: Int
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
            itemList[item] = totalAmount
            // Add Item To SQL
            itemStackListCache = null
        }

        fun remove(item: GeneralItem, amount: Int) {
            val totalAmount = itemList.getOrDefault(item, 0) - amount
            if(totalAmount < 1){
                itemList.remove(item)
                // Clear Item From SQL
            } else {
                itemList[item] = totalAmount
                // Remove Item From SQL
            }
            itemStackListCache = null
        }

        fun getList(page: Int): List<ItemStack>? {
            return itemStackList.slice(page, maxPage)
        }
    }

    data class Equip(override val uuidPlayer: UUIDPlayer): ItemChest {
        override val sizeColumnName = "Equip"
        override val defaultMaxPage = 2
        private val itemList = mutableListOf<EquipItem>()

        init {
            // Load Item From SQL
        }

        fun add(item: EquipItem) {
            itemList.add(item)
            // Add Item To SQL
        }

        fun remove(item: EquipItem) {
            itemList.remove(item)
            // Remove Item From SQL
        }

        fun getList(page: Int): List<EquipItem>? {
            return itemList.slice(page, maxPage)
        }
    }

    private companion object {
        private fun <T> List<T>.slice(page: Int, maxPage: Int): List<T>? {
            return when {
                page < 1 -> slice(1, maxPage)
                maxPage < page -> null
                else -> slice(((page - 1) * 27) until (page * 27))
            }
        }
    }
}