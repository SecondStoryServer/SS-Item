package me.syari.ss.item.equip.weapon

import me.syari.ss.battle.status.player.PlayerStatus
import me.syari.ss.battle.status.player.PlayerStatus.Companion.status
import me.syari.ss.battle.status.player.StatusChange
import me.syari.ss.battle.status.player.StatusType
import me.syari.ss.item.equip.EnhancedEquipItem
import org.bukkit.entity.Player

class EnhancedWeaponItem(
    override val data: WeaponItem, enhance: Int, enhancePlus: Int
): EnhancedEquipItem(
    data, enhance, enhancePlus
) {
    val damage = data.damage * enhanceRate
    val criticalChance = data.criticalChance

    fun getAttackStatus(player: Player): PlayerStatus {
        val playerStatus = player.status.clone()

        fun addStatusChange(statusType: StatusType, value: Float) {
            playerStatus.add(
                StatusChange.Cause.Equipment, statusType, value, StatusChange.Type.Add
            )
        }

        addStatusChange(StatusType.Attack(data.damageElementType), damage)
        addStatusChange(StatusType.CriticalChance, criticalChance)
        playerStatus.damageElementType = data.damageElementType
        return playerStatus
    }

    override val statusDescription = listOf(
        "属性" to data.damageElementType.display, "攻撃力" to "+$damage", "クリティカル率" to "+${criticalChance * 100}%"
    )
}