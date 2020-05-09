package me.syari.ss.item.equip.weapon.indirect

import me.syari.ss.battle.equipment.ElementType
import me.syari.ss.item.EventListener.setProjectileStatus
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
            val ball = player.launchProjectile(Snowball::class.java)
            setProjectileStatus(ball, getAttackStatus(player))
        }
    }
}