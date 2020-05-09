package me.syari.ss.item.chest

import me.syari.ss.core.player.UUIDPlayer
import org.bukkit.OfflinePlayer

data class PlayerChestData(val uuidPlayer: UUIDPlayer) {
    val general by lazy { ItemChest.General(uuidPlayer) }
    val equip by lazy { ItemChest.Equip(uuidPlayer) }
    val compass by lazy { ItemChest.Compass(uuidPlayer) }

    companion object {
        private val chestDataMap = mutableMapOf<UUIDPlayer, PlayerChestData>()

        val OfflinePlayer.chestData
            get() = UUIDPlayer(this).chestData

        private val UUIDPlayer.chestData
            get() = chestDataMap.getOrPut(this) { PlayerChestData(this) }
    }
}