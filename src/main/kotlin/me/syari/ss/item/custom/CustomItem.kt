package me.syari.ss.item.custom

import me.syari.ss.core.auto.OnEnable
import me.syari.ss.core.item.CustomItemStack
import me.syari.ss.item.Main.Companion.itemPlugin
import me.syari.ss.item.custom.register.RegisterFunction
import me.syari.ss.item.custom.register.ItemRegister
import org.bukkit.Material
import org.bukkit.persistence.PersistentDataType

interface CustomItem {
    val id: String
    val material: Material
    val display: String
    val itemType: ItemType
    val description: String

    val itemStack: CustomItemStack
        get() = CustomItemStack.create(
            material, display, "&6アイテムタイプ: $itemType", "", *description.lines().map { "&7$it" }.toTypedArray()
        )

    companion object {
        private const val itemIdPersistentKey = "ss-item-id"

        fun getId(item: CustomItemStack): String? {
            return item.getPersistentData(itemPlugin)?.get(itemIdPersistentKey, PersistentDataType.STRING)
        }

        fun from(id: String): CustomItem? {
            return ItemRegister.getCustomItem(id)
        }

        fun from(item: CustomItemStack): CustomItem? {
            return getId(item)?.let {
                from(it)
            }
        }

        fun reload() {
            ItemRegister.clearAll()
            RegisterFunction.registerAll()
        }
    }
}