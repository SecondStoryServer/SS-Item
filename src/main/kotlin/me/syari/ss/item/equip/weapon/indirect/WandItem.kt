package me.syari.ss.item.equip.weapon.indirect

import me.syari.ss.battle.equipment.ElementType
import me.syari.ss.battle.status.player.StatusType
import me.syari.ss.item.custom.ItemType
import me.syari.ss.item.equip.weapon.WeaponItem
import me.syari.ss.item.equip.weapon.WeaponType
import org.bukkit.Material

data class WandItem(
    override val id: String,
    override val material: Material,
    override val display: String,
    override val description: String,
    override val onAttackStatus: Map<StatusType, Float>,
    override val damageElementType: ElementType?
): WeaponItem {
    override val itemType = ItemType.Weapon(WeaponType.Wand)
}