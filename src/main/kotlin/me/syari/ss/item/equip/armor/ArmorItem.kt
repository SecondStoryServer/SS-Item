package me.syari.ss.item.equip.armor

import me.syari.ss.battle.equipment.ElementType
import me.syari.ss.item.custom.ItemType
import me.syari.ss.item.equip.EquipItem
import org.bukkit.Material

class ArmorItem(
    override val id: String,
    override val material: Material,
    override val display: String,
    override val description: String,
    val defense: Float,
    val defenseElementType: ElementType
): EquipItem {
    override val itemType = ItemType.Armor
}