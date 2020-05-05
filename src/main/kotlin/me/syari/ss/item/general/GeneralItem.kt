package me.syari.ss.item.general

import me.syari.ss.core.auto.OnEnable
import me.syari.ss.core.item.CustomItemStack
import me.syari.ss.item.general.potion.HealPotion
import org.bukkit.ChatColor
import org.bukkit.Material

interface GeneralItem {
    val id: String
    val material: Material
    val display: String
    val itemType: ItemType
    val description: String

    val itemStack: CustomItemStack
        get() = CustomItemStack.create(
            material,
            display,
            "&6アイテムタイプ: $itemType",
            "",
            *description.lines().map { "&7$it" }.toTypedArray()
        )

    enum class ItemType(val color: ChatColor, val display: String) {
        Potion(ChatColor.LIGHT_PURPLE, "ポーション");

        override fun toString() = "$color$display"
    }

    companion object {
        private val idToList = mutableMapOf<String, GeneralItem>()

        fun clear(){
            idToList.clear()
        }

        fun register(id: String, item: GeneralItem){
            idToList[id] = item
        }

        fun getById(id: String): GeneralItem? {
            return idToList[id]
        }
    }

    object Register: OnEnable {
        override fun onEnable() {
            reload()
        }

        fun reload(){
            clear()
            HealPotion.register()
        }
    }
}