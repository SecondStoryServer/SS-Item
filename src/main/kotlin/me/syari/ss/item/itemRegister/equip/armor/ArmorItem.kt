package me.syari.ss.item.itemRegister.equip.armor

import me.syari.ss.battle.equipment.ElementType
import me.syari.ss.core.item.CustomItemStack
import me.syari.ss.item.ItemRarity
import me.syari.ss.item.itemRegister.custom.ItemType
import me.syari.ss.item.itemRegister.equip.EnhancedEquipItem.Companion.getEnhance
import me.syari.ss.item.itemRegister.equip.EquipItem
import org.bukkit.Material
import java.util.UUID

class ArmorItem(
    override val id: String,
    override val material: Material,
    override val display: String,
    override val description: String,
    override val rarity: ItemRarity,
    val defense: Float,
    val defenseElementType: ElementType
): EquipItem {
    override val itemType = ItemType.Armor
    override val sortNumber = 0
    val armorType = ArmorType.from(material)

    override fun getEnhanced(item: CustomItemStack): EnhancedArmorItem {
        return getEnhanced(null, getEnhance(item))
    }

    override fun getEnhanced(
        uuid: UUID?,
        enhance: Int
    ): EnhancedArmorItem {
        return EnhancedArmorItem(this, uuid, enhance)
    }

    override fun compareTo(other: EquipItem): Int {
        return if (other is ArmorItem) {
            armorType.compareTo(other.armorType)
        } else {
            super.compareTo(other)
        }
    }

    enum class ArmorType {
        BOOTS,
        LEGGINGS,
        CHESTPLATE,
        HELMET;

        companion object {
            fun from(type: Material): ArmorType {
                return when (type) {
                    Material.LEATHER_BOOTS, Material.CHAINMAIL_BOOTS, Material.IRON_BOOTS, Material.GOLDEN_BOOTS, Material.DIAMOND_BOOTS -> BOOTS

                    Material.LEATHER_LEGGINGS, Material.CHAINMAIL_LEGGINGS, Material.IRON_LEGGINGS, Material.GOLDEN_LEGGINGS, Material.DIAMOND_LEGGINGS -> LEGGINGS

                    Material.LEATHER_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.IRON_CHESTPLATE, Material.GOLDEN_CHESTPLATE, Material.DIAMOND_CHESTPLATE -> CHESTPLATE

                    else -> HELMET
                }
            }
        }
    }
}