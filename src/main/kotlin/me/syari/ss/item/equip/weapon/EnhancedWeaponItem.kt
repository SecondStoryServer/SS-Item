package me.syari.ss.item.equip.weapon

import me.syari.ss.battle.status.player.PlayerStatus
import me.syari.ss.battle.status.player.PlayerStatus.Companion.status
import me.syari.ss.battle.status.player.StatusChange
import me.syari.ss.battle.status.player.StatusType
import me.syari.ss.core.item.CustomItemStack
import me.syari.ss.item.equip.EnhancedEquipItem
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemFlag
import java.util.UUID

class EnhancedWeaponItem(
    override val data: WeaponItem, enhance: Int
): EnhancedEquipItem(
    data, enhance
) {
    private val damage = data.damage * enhanceRate
    private val criticalChance = data.criticalChance
    override val statusChange = mapOf(
        StatusType.Attack(data.damageElementType) to ("+$damage" to damage),
        StatusType.CriticalChance to ("+${criticalChance * 100}%" to criticalChance)
    )

    override val itemStack: CustomItemStack
        get() = super.itemStack.apply {
            editLore {
                add("&6攻撃速度: $attackSpeedBar")
            }
            val changeAttackSpeed = getDefaultAttackSpeed(type) - data.attackSpeed
            if (changeAttackSpeed != 0.0) {
                editMeta {
                    val modifier = AttributeModifier(
                        UUID.randomUUID(),
                        "ss-item",
                        changeAttackSpeed,
                        AttributeModifier.Operation.ADD_NUMBER,
                        EquipmentSlot.HAND
                    )
                    addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, modifier)
                    addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                }
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
        var leftAmount = ((4.0F - data.attackSpeed) * 10).toInt()
        when {
            leftAmount < 0 -> leftAmount = 0
            40 < leftAmount -> leftAmount = 40
        }
        val rightAmount = 40 - leftAmount
        attackSpeedBar = buildString {
            append("&7")
            for (i in 0 until rightAmount) {
                append("|")
            }
            append("&c")
            for (i in 0 until leftAmount) {
                append("|")
            }
        }
    }

    companion object {
        // https://minecraft.gamepedia.com/Damage#Dealing_damage
        private val materialToAttackSpeed = mapOf(
            Material.WOODEN_SWORD to 1.6,
            Material.GOLDEN_SWORD to 1.6,
            Material.STONE_SWORD to 1.6,
            Material.IRON_SWORD to 1.6,
            Material.DIAMOND_SWORD to 1.6,
            Material.TRIDENT to 1.1,
            Material.WOODEN_SHOVEL to 1.0,
            Material.GOLDEN_SHOVEL to 1.0,
            Material.STONE_SHOVEL to 1.0,
            Material.IRON_SHOVEL to 1.0,
            Material.DIAMOND_SHOVEL to 1.0,
            Material.WOODEN_PICKAXE to 1.2,
            Material.GOLDEN_PICKAXE to 1.2,
            Material.STONE_PICKAXE to 1.2,
            Material.IRON_PICKAXE to 1.2,
            Material.DIAMOND_PICKAXE to 1.2,
            Material.WOODEN_AXE to 0.8,
            Material.GOLDEN_AXE to 1.0,
            Material.STONE_AXE to 0.8,
            Material.IRON_AXE to 0.9,
            Material.DIAMOND_AXE to 1.0,
            Material.WOODEN_HOE to 1.0,
            Material.GOLDEN_HOE to 1.0,
            Material.STONE_HOE to 2.0,
            Material.IRON_HOE to 3.0,
            Material.DIAMOND_HOE to 4.0
        )

        fun getDefaultAttackSpeed(material: Material): Double {
            return materialToAttackSpeed.getOrDefault(material, 4.0)
        }

        fun getAttackSpeedCoolDownTick(attackSpeed: Float): Long {
            val seconds = 1.0F / attackSpeed
            return (20 * seconds).toLong()
        }
    }
}