package me.syari.ss.item.itemRegister.compass

import me.syari.ss.item.ItemRarity
import me.syari.ss.item.itemRegister.custom.CustomItem
import me.syari.ss.item.itemRegister.custom.ItemType
import me.syari.ss.item.itemRegister.custom.register.ItemRegister
import org.bukkit.Location
import org.bukkit.Material

data class CompassItem(
    override val id: String, val teleportToName: String, val teleportTo: Location
): CustomItem {
    override val itemType = ItemType.Compass
    override val material = Material.COMPASS
    override val display = "&a$teleportToName"
    override val description = "船をがあれば、$teleportToName へ行くことが出来る"
    override val rarity: ItemRarity? = null

    override fun register() {
        register(id, this)
    }

    companion object: ItemRegister<CompassItem>() {
        val allCompass get() = idToList.values.toList()
    }
}