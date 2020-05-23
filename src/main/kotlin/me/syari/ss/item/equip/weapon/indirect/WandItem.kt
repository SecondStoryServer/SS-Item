package me.syari.ss.item.equip.weapon.indirect

import me.syari.ss.battle.damage.DamageCalculator
import me.syari.ss.battle.equipment.ElementType
import me.syari.ss.battle.status.EntityStatus
import me.syari.ss.core.item.CustomItemStack
import me.syari.ss.core.particle.CustomParticle
import me.syari.ss.item.ItemRarity
import me.syari.ss.item.custom.ClickableItem
import me.syari.ss.item.equip.weapon.EnhancedWeaponItem.Companion.getAttackSpeedCoolDownTick
import me.syari.ss.item.equip.weapon.WeaponItem
import me.syari.ss.item.equip.weapon.WeaponType
import org.bukkit.Material
import org.bukkit.entity.Player

class WandItem(
    override val id: String,
    override val material: Material,
    override val display: String,
    override val description: String,
    override val rarity: ItemRarity,
    override val damageElementType: ElementType,
    override val damage: Float,
    override val criticalChance: Float,
    override val attackSpeed: Float
): WeaponItem, ClickableItem {
    override val weaponType = WeaponType.Wand
    override val coolDownTime = getAttackSpeedCoolDownTick(attackSpeed)
    override val coolDownType = ClickableItem.CoolDownType.Weapon

    override fun onClick(player: Player, item: CustomItemStack, clickType: ClickableItem.ClickType): Boolean {
        if (clickType.isRight) {
            launchLaser(player, item)
            return true
        }
        return false
    }

    private fun launchLaser(player: Player, item: CustomItemStack) {
        val particle = CustomParticle.RedStone(155, 89, 182, 10, 0.0)
        val maxDistance = 10
        val interDistance = 0.5
        val beginLocation = player.location
        val direction = beginLocation.direction.normalize()
        var t = 0.0
        while (t <= maxDistance) {
            t += interDistance
            val x = direction.x * t
            val y = direction.y * t + 1.5
            val z = direction.z * t
            val interLocation = beginLocation.clone().add(x, y, z)
            if (!interLocation.block.isPassable) {
                break
            }
            val nearEntity = interLocation.getNearbyLivingEntities(interDistance)
            if (nearEntity.isNotEmpty()) {
                val nearestEntity = nearEntity.first()
                val victimStatus = EntityStatus.from(nearestEntity)
                val enhancedItem = getEnhanced(item)
                val attackerStatus = enhancedItem.getAttackStatus(player)
                val damage = DamageCalculator.getDamage(attackerStatus, victimStatus)
                nearestEntity.damage(damage.toDouble(), player)
            }
            particle.spawn(interLocation)
        }
    }
}