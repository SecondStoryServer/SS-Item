package me.syari.ss.item.chest

import me.syari.ss.core.player.UUIDPlayer
import me.syari.ss.item.DatabaseConnector
import me.syari.ss.item.itemRegister.compass.CompassItem
import me.syari.ss.item.itemRegister.compass.CompassItem.Companion.allCompass
import me.syari.ss.item.itemRegister.equip.EnhancedEquipItem
import me.syari.ss.item.itemRegister.equip.armor.EnhancedArmorItem
import me.syari.ss.item.itemRegister.equip.weapon.EnhancedWeaponItem
import me.syari.ss.item.itemRegister.general.GeneralItem
import me.syari.ss.item.itemRegister.general.GeneralItemWithAmount
import java.util.UUID

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
        private val itemMap = DatabaseConnector.Chest.General.get(uuidPlayer).toMutableMap()
        private var itemList = itemMap.values.toMutableList()
        var sortType: SortType = SortType.Type
            set(value) {
                if (field == value) return
                field = value
                isSorted = false
            }
        var isReverse = false
            set(value) {
                if (field == value) return
                field = value
                isSorted = false
            }
        var isSorted = false
            private set

        fun sort() {
            itemList = sortType.sort(itemList, isReverse).toMutableList()
            isSorted = true
        }

        fun get(item: GeneralItem): GeneralItemWithAmount? {
            return itemMap[item]
        }

        private fun put(item: GeneralItem): GeneralItemWithAmount {
            if (isSorted) isSorted = false
            return GeneralItemWithAmount(item, 0).apply {
                itemList.add(this)
                itemMap[item] = this
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

        interface SortType {
            fun sort(itemList: List<GeneralItemWithAmount>, isReverse: Boolean): List<GeneralItemWithAmount>

            val isType
                get() = this == Type
            val isRarity
                get() = this == Rarity

            object Type: SortType {
                override fun sort(
                    itemList: List<GeneralItemWithAmount>, isReverse: Boolean
                ): List<GeneralItemWithAmount> {
                    return itemList.sortedBy(isReverse) { it.data }
                }
            }

            object Rarity: SortType {
                override fun sort(
                    itemList: List<GeneralItemWithAmount>, isReverse: Boolean
                ): List<GeneralItemWithAmount> {
                    return itemList.sortedBy(isReverse) { it.data.rarity }
                }
            }
        }
    }

    data class Equip(override val uuidPlayer: UUIDPlayer): ItemChest {
        override val sizeColumnName = "Equip"
        override val defaultMaxPage = 2
        private var itemList = DatabaseConnector.Chest.Equip.get(uuidPlayer).toMutableList()
        private val itemMap = itemList.mapNotNull { item -> item.uuid?.let { it to item } }.toMap().toMutableMap()
        var sortType: SortType = SortType.Type
            set(value) {
                if (field == value) return
                field = value
                isSorted = false
            }
        var isReverse = false
            set(value) {
                if (field == value) return
                field = value
                isSorted = false
            }
        var isSorted = false
            private set

        fun sort() {
            itemList = sortType.sort(itemList, isReverse).toMutableList()
            isSorted = true
        }

        fun add(item: EnhancedEquipItem) {
            if (isSorted) isSorted = false
            item.uuid?.let { uuid ->
                itemList.add(item)
                itemMap[uuid] = item
                DatabaseConnector.Chest.Equip.add(uuidPlayer, item)
            }
        }

        fun remove(item: EnhancedEquipItem) {
            if (isSorted) isSorted = false
            item.uuid?.let { uuid ->
                itemList.remove(item)
                itemMap.remove(uuid)
                DatabaseConnector.Chest.Equip.remove(uuidPlayer, item)
            }
        }

        fun getList(page: Int): List<EnhancedEquipItem>? {
            return itemList.slice(page, maxPage)
        }

        fun getItem(uuid: UUID): EnhancedEquipItem? {
            return itemMap[uuid]
        }

        interface SortType {
            fun sort(
                itemList: List<EnhancedEquipItem>, isReverse: Boolean
            ): List<EnhancedEquipItem>

            val isType
                get() = this == Type
            val isEnhance
                get() = this == Enhance
            val isRarity
                get() = this == Rarity
            val isStatus
                get() = this == Status

            object Type: SortType {
                override fun sort(
                    itemList: List<EnhancedEquipItem>, isReverse: Boolean
                ): List<EnhancedEquipItem> {
                    val groupByType = itemList.groupBy { it.data }
                    return groupByType.values.map { Rarity.sort(it, isReverse) }.flatten()
                }
            }

            object Enhance: SortType {
                override fun sort(
                    itemList: List<EnhancedEquipItem>, isReverse: Boolean
                ): List<EnhancedEquipItem> {
                    return itemList.sortedBy(isReverse) { it.enhance }
                }
            }

            object Rarity: SortType {
                override fun sort(
                    itemList: List<EnhancedEquipItem>, isReverse: Boolean
                ): List<EnhancedEquipItem> {
                    return itemList.sortedBy(isReverse) { it.data.rarity }
                }
            }

            object Status: SortType {
                override fun sort(
                    itemList: List<EnhancedEquipItem>, isReverse: Boolean
                ): List<EnhancedEquipItem> {
                    val weaponList = mutableListOf<EnhancedWeaponItem>()
                    val armorList = mutableListOf<EnhancedArmorItem>()
                    itemList.forEach { item ->
                        when (item) {
                            is EnhancedWeaponItem -> weaponList.add(item)
                            is EnhancedArmorItem -> armorList.add(item)
                        }
                    }
                    weaponList.sortBy(isReverse) { it.data.damage * it.enhanceRate }
                    armorList.sortBy(isReverse) { it.data.defense * it.enhanceRate }
                    return weaponList + armorList
                }
            }
        }
    }

    data class Compass(override val uuidPlayer: UUIDPlayer): ItemChest {
        override val sizeColumnName: String? = null
        override val defaultMaxPage = 1
        private val itemList = DatabaseConnector.Chest.Compass.get(uuidPlayer).toMutableSet()
        var displayMode: DisplayMode = DisplayMode.Both

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
            return displayMode.getList(this, page)
        }

        interface DisplayMode {
            fun getList(chest: Compass, page: Int): Map<CompassItem, Boolean>?

            val isBoth
                get() = this == Both
            val isOnlyHave
                get() = this == OnlyHave

            object Both: DisplayMode {
                override fun getList(chest: Compass, page: Int): Map<CompassItem, Boolean>? {
                    return allCompass.slice(page, chest.maxPage)?.associate { it to chest.has(it) }
                }
            }

            object OnlyHave: DisplayMode {
                override fun getList(chest: Compass, page: Int): Map<CompassItem, Boolean>? {
                    return allCompass.filter { chest.has(it) }.map { it to true }.toMap()
                }
            }
        }
    }

    private companion object {
        fun <T> List<T>.slice(page: Int, maxPage: Int?): List<T>? {
            return when {
                page < 1 -> slice(1, maxPage)
                maxPage != null && maxPage < page -> null
                else -> {
                    val mayBeBegin = (page - 1) * 27
                    val mayBeEnd = page * 27
                    val (begin, end) = when {
                        size < mayBeBegin -> 0 to 0
                        size < mayBeEnd -> mayBeBegin to size
                        else -> mayBeBegin to mayBeEnd
                    }
                    slice(begin until end)
                }
            }
        }

        inline fun <T, R: Comparable<R>> MutableList<T>.sortBy(
            isReverse: Boolean, crossinline selector: (T) -> R?
        ) {
            sortBy(selector)
            if (isReverse) reverse()
        }

        inline fun <T, R: Comparable<R>> Iterable<T>.sortedBy(
            isReverse: Boolean, crossinline selector: (T) -> R?
        ): List<T> {
            return if (isReverse) sortedBy(selector).reversed() else sortedBy(selector)
        }
    }
}