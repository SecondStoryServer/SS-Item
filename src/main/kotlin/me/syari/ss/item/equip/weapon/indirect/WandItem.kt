package me.syari.ss.item.equip.weapon.indirect

import me.syari.ss.battle.equipment.ElementType
import me.syari.ss.core.item.CustomItemStack
import me.syari.ss.core.particle.CustomParticle
import me.syari.ss.core.scheduler.CustomScheduler.runTimer
import me.syari.ss.item.EventListener.setProjectileStatus
import me.syari.ss.item.Main.Companion.itemPlugin
import me.syari.ss.item.custom.ClickableItem
import me.syari.ss.item.custom.ItemType
import me.syari.ss.item.equip.weapon.EnhancedWeaponItem.Companion.getAttackSpeedCoolDownTick
import me.syari.ss.item.equip.weapon.WeaponItem
import me.syari.ss.item.equip.weapon.WeaponType
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.entity.Snowball

class WandItem(
    override val id: String,
    override val material: Material,
    override val display: String,
    override val description: String,
    override val damageElementType: ElementType,
    override val damage: Float,
    override val criticalChance: Float,
    override val attackSpeed: Float
): WeaponItem, ClickableItem {
    override val itemType = ItemType.Weapon(WeaponType.Wand)
    override val coolDownTime = getAttackSpeedCoolDownTick(attackSpeed)

    override fun onClick(player: Player, item: CustomItemStack, clickType: ClickableItem.Type) {
        if (clickType.isRight) {
            val snowball = player.launchProjectile(Snowball::class.java)
            setProjectileStatus(snowball, getEnhanced(item).getAttackStatus(player))
            applyParticle(snowball)
        }
    }

    private fun applyParticle(snowball: Snowball) {
        val particle = CustomParticle.RedStone(155, 89, 182, 10, 0.0)
        runTimer(itemPlugin, 10) {
            if (snowball.isValid) {
                cancel()
            } else {
                particle.spawn(snowball)
            }
        }
    }
}