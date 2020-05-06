package me.syari.ss.item.custom.register

import me.syari.ss.core.item.CustomItemStack
import me.syari.ss.item.custom.CustomItem

open class RegisterList<T: CustomItem> {
    private val idToList = mutableMapOf<String, T>()

    fun clear(){
        idToList.clear()
    }

    fun register(id: String, item: T){
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
        private val list = mutableSetOf<RegisterList<out CustomItem>>()

        fun getCustomItem(id: String): CustomItem? {
            list.forEach { registerList ->
                registerList.from(id)?.let {
                    return it
                }
            }
            return null
        }

        fun clearAll(){
            list.forEach {
                it.clear()
            }
        }

        fun add(vararg registerList: RegisterList<out CustomItem>){
            list.addAll(registerList)
        }
    }
}