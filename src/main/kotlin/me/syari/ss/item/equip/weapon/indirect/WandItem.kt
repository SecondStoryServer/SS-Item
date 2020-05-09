package me.syari.ss.item.equip.weapon.indirect

import me.syari.ss.battle.equipment.ElementType
import me.syari.ss.core.particle.CustomParticle
import me.syari.ss.core.particle.CustomParticleList
import me.syari.ss.core.scheduler.CustomScheduler.runTimer
import me.syari.ss.item.EventListener.setProjectileStatus
import me.syari.ss.item.Main.Companion.itemPlugin
import me.syari.ss.item.custom.ClickableItem
import me.syari.ss.item.custom.ItemType
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

    override fun onClick(player: Player, clickType: ClickableItem.Type) {
        if(clickType.isRight){
            val snowball = player.launchProjectile(Snowball::class.java)
            setProjectileStatus(snowball, getAttackStatus(player))
            applyParticle(snowball)
        }
    }

    private fun applyParticle(snowball: Snowball){
        val particle = CustomParticle.RedStone(155, 89, 182, 10, 0.0)
        runTimer(itemPlugin, 10){
            if(snowball.isValid) {
                cancel()
            } else {
                particle.spawn(snowball)
            }
        }
    }
}