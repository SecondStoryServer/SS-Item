package me.syari.ss.item.equip.armor

import me.syari.ss.battle.status.StatusType
import me.syari.ss.item.equip.EnhancedEquipItem

class EnhancedArmorItem(
    override val data: ArmorItem, enhance: Int
): EnhancedEquipItem(
    data, enhance
) {
    private val defense = data.defense * enhanceRate
    override val statusChange = mapOf<StatusType, Pair<String, Float>>(
        StatusType.Attack(data.defenseElementType) to ("+$defense" to defense)
    )
}