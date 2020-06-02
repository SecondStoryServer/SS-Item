package me.syari.ss.item.itemRegister.custom.register

import me.syari.ss.core.item.CustomItemStack
import me.syari.ss.item.itemRegister.custom.CustomItem

open class ItemRegister<T: CustomItem> {
    protected val idToList = mutableMapOf<String, T>()

    fun clear() {
        idToList.clear()
    }

    fun register(id: String, item: T) {
        idToList[id] = item
    }

    fun from(id: String): T? {
        return idToList[id]
    }

    fun from(item: CustomItemStack): T? {
        return CustomItem.getId(item)?.let {
            from(it)
        }
    }

    companion object {
        private val list = mutableSetOf<ItemRegister<out CustomItem>>()

        fun getCustomItem(id: String): CustomItem? {
            list.forEach { registerList ->
                registerList.from(id)?.let {
                    return it
                }
            }
            return null
        }

        fun clearAll() {
            list.forEach {
                it.clear()
            }
        }

        fun add(vararg itemRegister: ItemRegister<out CustomItem>) {
            list.addAll(itemRegister)
        }
    }
}