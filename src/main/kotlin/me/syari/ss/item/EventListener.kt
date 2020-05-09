package me.syari.ss.item

import me.syari.ss.battle.damage.DamageCalculator
import me.syari.ss.battle.status.EntityStatus
import me.syari.ss.battle.status.player.PlayerStatus
import me.syari.ss.battle.status.player.PlayerStatus.Companion.status
import me.syari.ss.core.auto.Event
import me.syari.ss.core.item.CustomItemStack
import me.syari.ss.item.Main.Companion.itemPlugin
import me.syari.ss.item.custom.ClickableItem
import me.syari.ss.item.custom.CustomItem
import me.syari.ss.item.equip.weapon.WeaponItem.Companion.projectileShooterStatusMetaDataKey
import me.syari.ss.item.equip.weapon.indirect.BowItem
import me.syari.ss.item.equip.weapon.melee.MeleeItem
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.metadata.FixedMetadataValue

object EventListener: Event {
    @EventHandler
    fun on(e: PlayerInteractEvent) {
        val item = e.item?.let {
            CustomItemStack.create(it)
        } ?: return
        val customItem = CustomItem.from(item) ?: return
        if (customItem is ClickableItem) {
            val player = e.player
            val clickType = when (e.action) {
                Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK -> ClickableItem.Type.Left
                Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK -> ClickableItem.Type.Right
                else -> return
            }
            customItem.onClick(player, clickType)
        }
    }

    fun setProjectileStatus(projectile: Entity, status: EntityStatus){
        val metadataValue = FixedMetadataValue(itemPlugin, status)
        projectile.setMetadata(projectileShooterStatusMetaDataKey, metadataValue)
    }

    @EventHandler
    fun on(e: EntityShootBowEvent){
        val entity = e.entity
        val entityStatus = if(entity is Player){
            val item = e.bow?.let {
                CustomItemStack.create(it)
            } ?: return
            val customItem = CustomItem.from(item) ?: return
            if(customItem !is BowItem) return
            e.consumeArrow = false
            customItem.getAttackStatus(entity)
        } else {
            EntityStatus.from(entity)
        } ?: return
        setProjectileStatus(e.projectile, entityStatus)
    }

    @EventHandler
    fun on(e: EntityDamageByEntityEvent){
        val attacker = e.damager
        val victim = e.entity
        val victimStatus = EntityStatus.from(victim) ?: return
        val attackerStatus = when(attacker){
            is Projectile -> {
                val metaDataValueList = attacker.getMetadata(projectileShooterStatusMetaDataKey)
                val metaDataValue = metaDataValueList.firstOrNull() ?: return
                metaDataValue.value() as? EntityStatus ?: return
            }
            is Player -> {
                val weapon = CustomItemStack.create(attacker.inventory.itemInMainHand)
                val customItem = CustomItem.from(weapon)
                if(customItem is MeleeItem){
                    customItem.getAttackStatus(attacker)
                } else {
                    attacker.status
                }
            }
            else -> {
                return
            }
        }
        e.damage = DamageCalculator.getDamage(attackerStatus, victimStatus).toDouble()
    }
}