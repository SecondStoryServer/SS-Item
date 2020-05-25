package me.syari.ss.item.chest

import me.syari.ss.core.player.UUIDPlayer
import me.syari.ss.item.DatabaseConnector
import me.syari.ss.item.compass.CompassItem
import me.syari.ss.item.compass.CompassItem.Companion.allCompass
import me.syari.ss.item.equip.EnhancedEquipItem
import me.syari.ss.item.equip.armor.EnhancedArmorItem
import me.syari.ss.item.equip.weapon.EnhancedWeaponItem
import me.syari.ss.item.general.GeneralItem
import me.syari.ss.item.general.GeneralItemWithAmount

interface ItemChest {
    val uuidPlayer: UUIDPlayer
    val sizeColumnName: String?
    val defaultMaxPage: Int

    var maxPage: Int
        get() = DatabaseConnector.Chest.MaxPage.get(uuidPlayer, this) ?: defaultMaxPage
        set(value) {
            DatabaseConnector.Chest.MaxPage.set(uuidPlayer, this, value)
        }

    data class General(override val uuidPlayer: UUIDPlayer): ItemChest {
        override val sizeColumnName = "General"
        override val defaultMaxPage = 2
        private var itemList = DatabaseConnector.Chest.General.get(uuidPlayer).toMutableList()
        var sortType = SortType.Type
        private var isSorted = false

        fun checkSort() {
            if (isSorted) return
            itemList = sortType.sort(itemList).toMutableList()
        }

        fun get(item: GeneralItem): GeneralItemWithAmount? {
            return itemList.firstOrNull { it.data == item }
        }

        private fun put(item: GeneralItem): GeneralItemWithAmount {
            if (isSorted) isSorted = false
            return GeneralItemWithAmount(item, 0).apply {
                itemList.add(this)
            }
        }

        fun getAmount(item: GeneralItem): Int? {
            return get(item)?.amount
        }

        fun getAmount(item: GeneralItem, default: Int): Int {
            return getAmount(item) ?: default
        }

        fun add(item: GeneralItem, amount: Int) {
            val totalAmount = getAmount(item, 0) + amount
            set(item, totalAmount)
        }

        fun remove(item: GeneralItem, amount: Int) {
            val totalAmount = getAmount(item, 0) - amount
            set(item, totalAmount)
        }

        fun set(item: GeneralItem, amount: Int) {
            val itemWithAmount = get(item)
            if (0 < amount) {
                (itemWithAmount ?: put(item)).amount = amount
            } else if (itemWithAmount != null) {
                if (isSorted) isSorted = false
                itemList.remove(itemWithAmount)
            }
            DatabaseConnector.Chest.General.set(uuidPlayer, item, amount)
        }

        fun getList(page: Int): List<GeneralItemWithAmount>? {
            return itemList.slice(page, maxPage)
        }

        enum class SortType {
            Type,
            Rarity;

            fun sort(itemList: List<GeneralItemWithAmount>): List<GeneralItemWithAmount> {
                return when (this) {
                    Type -> sortByType(itemList)
                    Rarity -> sortByRarity(itemList)
                }
            }

            companion object {
                private fun sortByType(itemList: List<GeneralItemWithAmount>): List<GeneralItemWithAmount> {
                    return itemList.sortedBy { it.data }
                }

                private fun sortByRarity(itemList: List<GeneralItemWithAmount>): List<GeneralItemWithAmount> {
                    return itemList.sortedBy { it.data.rarity }
                }
            }
        }
    }

    data class Equip(override val uuidPlayer: UUIDPlayer): ItemChest {
        override val sizeColumnName = "Equip"
        override val defaultMaxPage = 2
        private var itemList = DatabaseConnector.Chest.Equip.get(uuidPlayer).toMutableList()
        var sortType: SortType = SortType.Type
        private var isSorted = false

        fun checkSort() {
            if (isSorted) return
            itemList = sortType.sort(itemList).toMutableList()
        }

        fun add(item: EnhancedEquipItem) {
            if (isSorted) isSorted = false
            itemList.add(item)
            DatabaseConnector.Chest.Equip.add(uuidPlayer, item)
        }

        fun remove(item: EnhancedEquipItem) {
            if (isSorted) isSorted = false
            itemList.remove(item)
            DatabaseConnector.Chest.Equip.remove(uuidPlayer, item)
        }

        fun getList(page: Int): List<EnhancedEquipItem>? {
            return itemList.slice(page, maxPage)
        }

        enum class SortType {
            Type,
            Enhance,
            Rarity,
            Status;

            fun sort(itemList: List<EnhancedEquipItem>): List<EnhancedEquipItem> {
                return when (this) {
                    Type -> sortByType(itemList)
                    Enhance -> sortByEnhance(itemList)
                    Rarity -> sorByRarity(itemList)
                    Status -> sortByStatus(itemList)
                }
            }

            companion object {
                private fun sortByType(itemList: List<EnhancedEquipItem>): List<EnhancedEquipItem> {
                    val groupByType = itemList.groupBy { it.data }
                    return groupByType.values.map { sorByRarity(it) }.flatten()
                }

                private fun sortByEnhance(itemList: List<EnhancedEquipItem>): List<EnhancedEquipItem> {
                    return itemList.sortedBy { it.enhance }
                }

                private fun sorByRarity(itemList: List<EnhancedEquipItem>): List<EnhancedEquipItem> {
                    return itemList.sortedBy { it.data.rarity }
                }

                private fun sortByStatus(itemList: List<EnhancedEquipItem>): List<EnhancedEquipItem> {
                    val weaponList = mutableListOf<EnhancedWeaponItem>()
                    val armorList = mutableListOf<EnhancedArmorItem>()
                    itemList.forEach { item ->
                        when (item) {
                            is EnhancedWeaponItem -> weaponList.add(item)
                            is EnhancedArmorItem -> armorList.add(item)
                        }
                    }
                    weaponList.sortBy { it.data.damage * it.enhanceRate }
                    armorList.sortBy { it.data.defense * it.enhanceRate }
                    return weaponList + armorList
                }
            }
        }
    }

    data class Compass(override val uuidPlayer: UUIDPlayer): ItemChest {
        override val sizeColumnName: String? = null
        override val defaultMaxPage = 1
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