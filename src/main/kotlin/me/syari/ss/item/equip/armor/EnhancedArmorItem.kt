package me.syari.ss.item.equip.armor

import me.syari.ss.battle.status.player.StatusType
import me.syari.ss.item.equip.EnhancedEquipItem

class EnhancedArmorItem(
    override val data: ArmorItem, enhance: Int, enhancePlus: Int
): EnhancedEquipItem(
    data, enhance, enhancePlus
) {
    private val damage = data.defense * enhanceRate
    override val statusChange = mapOf<StatusType, Pair<String, Float>>(
        StatusType.Attack(data.defenseElementType) to ("+$damage" to damage)
    )
}