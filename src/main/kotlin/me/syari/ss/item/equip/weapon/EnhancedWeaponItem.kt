package me.syari.ss.item.equip.weapon

import me.syari.ss.battle.status.player.PlayerStatus
import me.syari.ss.battle.status.player.PlayerStatus.Companion.status
import me.syari.ss.battle.status.player.StatusChange
import me.syari.ss.battle.status.player.StatusType
import me.syari.ss.core.item.CustomItemStack
import me.syari.ss.item.equip.EnhancedEquipItem
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import java.util.UUID

class EnhancedWeaponItem(
    override val data: WeaponItem, enhance: Int, enhancePlus: Int
): EnhancedEquipItem(
    data, enhance, enhancePlus
) {
    private val damage = data.damage * enhanceRate
    private val criticalChance = data.criticalChance
    override val statusChange = mapOf(
        StatusType.Attack(data.damageElementType) to ("+$damage" to damage),
        StatusType.CriticalChance to ("+${criticalChance * 100}%" to criticalChance)
    )

    override val itemStack: CustomItemStack
        get() = super.itemStack.apply {
            lore.add("&6攻撃速度: $attackSpeedBar")
            editMeta {
                val attributeModifiers = getAttributeModifiers(Attribute.GENERIC_ATTACK_SPEED)
                var changeAttackSpeed = data.attackSpeed.toDouble()
                attributeModifiers?.forEach {
                    if (it.operation == AttributeModifier.Operation.ADD_NUMBER) {
                        changeAttackSpeed -= it.amount
                    }
                }
                val modifier = AttributeModifier(
                    UUID.randomUUID(),
                    "ss-item",
                    changeAttackSpeed,
                    AttributeModifier.Operation.ADD_NUMBER,
                    EquipmentSlot.HAND
                )
                addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, modifier)
            }
        }

    fun getAttackStatus(player: Player): PlayerStatus {
        val playerStatus = player.status.clone()
        statusChange.forEach { (statusType, value) ->
            playerStatus.add(
                StatusChange.Cause.Equipment, statusType, value.second, StatusChange.Type.Add
            )
        }
        playerStatus.damageElementType = data.damageElementType
        return playerStatus
    }

    private val attackSpeedBar: String

    init {
        var rightAmount = ((4.0F - data.attackSpeed) * 10).toInt()
        when {
            rightAmount < 0 -> rightAmount = 0
            40 < rightAmount -> rightAmount = 40
        }
        val leftAmount = 40 - rightAmount
        attackSpeedBar = buildString {
            append("&6")
            for (i in 0 until rightAmount) {
                append("|")
            }
            append("&7")
            for (i in 0 until leftAmount) {
                append("|")
            }
        }
    }
}