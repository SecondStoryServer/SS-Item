package me.syari.ss.item.itemRegister.equip

import me.syari.ss.battle.status.StatusType
import me.syari.ss.core.item.CustomItemStack
import me.syari.ss.item.Main.Companion.itemPlugin
import me.syari.ss.item.itemRegister.custom.CustomItem
import org.bukkit.persistence.PersistentDataType
import java.util.UUID

open class EnhancedEquipItem protected constructor(
    open val data: EquipItem,
    val uuid: UUID?,
    val enhance: Int
): CustomItem {
    override val id by lazy { data.id + ":" + uuid }
    override val material by lazy { data.material }
    override val display by lazy { data.display }
    override val itemType by lazy { data.itemType }
    override val description by lazy { data.description }
    override val rarity by lazy { data.rarity }
    open val statusChange = mapOf<StatusType, Pair<String, Float>>()

    val enhanceRate
        get(): Float {
            val rateBegin = 0.8F
            val rateEnd = 1.0F
            return ((rateEnd - rateBegin) * (enhance / 100F)) + rateBegin
        }

    override val itemStack: CustomItemStack
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

    override fun register() {
        throw UnsupportedOperationException()
    }

    companion object {
        const val enhancePersistentDataKey = "ss-item-equip-enhance"

        fun getEnhance(item: CustomItemStack): Int {
            return item.getPersistentData(itemPlugin)?.get(enhancePersistentDataKey, PersistentDataType.INTEGER) ?: 0
        }
    }
}