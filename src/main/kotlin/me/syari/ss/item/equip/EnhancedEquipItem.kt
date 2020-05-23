package me.syari.ss.item.equip

import me.syari.ss.battle.status.StatusType
import me.syari.ss.core.item.CustomItemStack
import me.syari.ss.item.Main.Companion.itemPlugin
import org.bukkit.persistence.PersistentDataType

open class EnhancedEquipItem(
    open val data: EquipItem, val enhance: Int
) {
    open val statusChange = mapOf<StatusType, Pair<String, Float>>()

    val enhanceRate
        get(): Float {
            val rateBegin = 0.8F
            val rateEnd = 1.0F
            return ((rateEnd - rateBegin) * (enhance / 100F)) + rateBegin
        }

    open val itemStack: CustomItemStack
        get() = data.itemStack.apply {
            display += " &6+$enhance"
            editLore {
                add("")
                statusChange.forEach { (statusType, value) ->
                    add("${statusType.display}: &7${value.first}")
                }
            }
            editPersistentData(itemPlugin) {
                set(enhancePersistentDataKey, PersistentDataType.INTEGER, enhance)
            }
        }

    companion object {
        const val enhancePersistentDataKey = "ss-item-equip-enhance"

        fun getEnhance(item: CustomItemStack): Int {
            return item.getPersistentData(itemPlugin)?.get(enhancePersistentDataKey, PersistentDataType.INTEGER) ?: 0
        }
    }
}