package me.syari.ss.item

import me.syari.ss.battle.damage.DamageCalculator
import me.syari.ss.battle.status.EntityStatus
import me.syari.ss.battle.status.player.PlayerStatus.Companion.status
import me.syari.ss.core.auto.Event
import me.syari.ss.core.item.CustomItemStack
import me.syari.ss.core.player.UUIDPlayer
import me.syari.ss.core.scheduler.CustomScheduler.runLater
import me.syari.ss.item.Main.Companion.itemPlugin
import me.syari.ss.item.custom.ClickableItem
import me.syari.ss.item.custom.CustomItem
import me.syari.ss.item.equip.weapon.indirect.BowItem
import me.syari.ss.item.equip.weapon.melee.MeleeItem
import org.bukkit.entity.Arrow
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.metadata.FixedMetadataValue

object EventListener: Event {
    private val cancelClickEvent = mutableSetOf<Pair<UUIDPlayer, ClickableItem.CoolDownType>>()

    @EventHandler
    fun on(e: PlayerInteractEvent) {
        val item = e.item?.let {
            CustomItemStack.create(it)
        } ?: return
        val customItem = CustomItem.from(item) ?: return
        if (customItem is ClickableItem) {
            val player = e.player
            val clickType = when (e.action) {
                Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK -> ClickableItem.ClickType.Left
                Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK -> ClickableItem.ClickType.Right
                else -> return
            }
            val uuidPlayer = UUIDPlayer(player)
            val coolDownType = customItem.coolDownType
            val cancelClickEventKey = uuidPlayer to coolDownType
            if (cancelClickEvent.contains(cancelClickEventKey)) return
            val success = customItem.onClick(player, item, clickType)
            if (success) {
                cancelClickEvent.add(cancelClickEventKey)
                runLater(itemPlugin, customItem.coolDownTime) {
                    cancelClickEvent.remove(cancelClickEventKey)
                }
            }
        }
    }

    private const val arrowShooterStatusMetaDataKey = "ss-item-arrow-shooter-status"
    private const val arrowForceMetaDataKey = "ss-item-arrow-force"

    private fun setProjectileStatus(projectile: Entity, status: EntityStatus) {
        val metadataValue = FixedMetadataValue(itemPlugin, status)
        projectile.setMetadata(arrowShooterStatusMetaDataKey, metadataValue)
    }

    @EventHandler
    fun on(e: EntityShootBowEvent) {
        val entity = e.entity
        val entityStatus = if (entity is Player) {
            e.consumeArrow = false
            val item = e.bow?.let {
                CustomItemStack.create(it)
            } ?: return
            val bowItem = CustomItem.from(item) ?: return
            if (bowItem !is BowItem) return
            val enhancedBowItem = bowItem.getEnhanced(item)
            enhancedBowItem.getAttackStatus(entity)
        } else {
            EntityStatus.from(entity)
        } ?: return
        val arrow = e.projectile
        setProjectileStatus(arrow, entityStatus)
        arrow.setMetadata(arrowForceMetaDataKey, FixedMetadataValue(itemPlugin, e.force))
    }

    @EventHandler
    fun on(e: EntityDamageByEntityEvent) {
        val attacker = e.damager
        val victim = e.entity
        val victimStatus = EntityStatus.from(victim) ?: return
        val (attackerStatus, damageRate) = when (attacker) {
            is Arrow -> {
                val statusMetaDataValueList = attacker.getMetadata(arrowShooterStatusMetaDataKey)
                val statusMetaDataValue = statusMetaDataValueList.firstOrNull() ?: return
                val status = statusMetaDataValue.value() as? EntityStatus ?: return
                val forceMetadataValueList = attacker.getMetadata(arrowForceMetaDataKey)
                val force = forceMetadataValueList.firstOrNull()?.asFloat() ?: 1.0F
                status to force
            }
            is Player -> {
                val item = CustomItemStack.create(attacker.inventory.itemInMainHand)
                val customItem = CustomItem.from(item)
                if (customItem is MeleeItem) {
                    val enhancedMeleeItem = customItem.getEnhanced(item)
                    enhancedMeleeItem.getAttackStatus(attacker)
                } else {
                    attacker.status
                } to getDamageRate(attacker.attackCooldown)
            }
            else -> {
                return
            }
        }
        val damage = DamageCalculator.getDamage(attackerStatus, victimStatus) * damageRate
        e.damage = damage.toDouble()
    }

    private fun getDamageRate(attackCoolDown: Float): Float {
        val minDamageRate = 0.2F
        val maxDamageRate = 1.0F
        return ((maxDamageRate - minDamageRate) * attackCoolDown) + minDamageRate
    }
}