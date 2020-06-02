package me.syari.ss.item.itemRegister.equip.armor

import me.syari.ss.battle.status.StatusType
import me.syari.ss.item.itemRegister.equip.EnhancedEquipItem
import java.util.UUID

class EnhancedArmorItem(
    override val data: ArmorItem, uuid: UUID?, enhance: Int
): EnhancedEquipItem(
    data, uuid, enhance
) {
    private val defense = data.defense * enhanceRate
    override val statusChange = mapOf<StatusType, Pair<String, Float>>(
        StatusType.Attack(data.defenseElementType) to ("+$defense" to defense)
    )
}