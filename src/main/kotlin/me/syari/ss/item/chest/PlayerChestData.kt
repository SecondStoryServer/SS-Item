package me.syari.ss.item.chest

import me.syari.ss.core.player.UUIDPlayer

data class PlayerChestData(val uuidPlayer: UUIDPlayer){
    val general by lazy { ItemChest.General(uuidPlayer) }
    val equip by lazy { ItemChest.Equip(uuidPlayer) }
}