package me.syari.ss.item.vanilla

import me.syari.ss.core.item.InventoryBase64
import me.syari.ss.core.player.UUIDPlayer
import me.syari.ss.item.DatabaseConnector
import org.bukkit.GameMode
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory

object VanillaInventory {
    internal data class PlayerData(val uuidPlayer: UUIDPlayer) {
        private var contentsCache: Array<ItemStack?>? = null

        fun load(): Array<ItemStack?>? {
            return if (contentsCache == null) {
                val base64 = DatabaseConnector.VanillaInventory.getBase64(uuidPlayer) ?: return null
                val inventory = InventoryBase64.getInventoryFromBase64(base64)
                inventory.contents.apply {
                    contentsCache = this
                }
            } else {
                contentsCache
            }
        }

        fun save(playerInventory: PlayerInventory) {
            val contents = playerInventory.contents
            val base64 = if (contents.filterNotNull().isNotEmpty()) {
                InventoryBase64.toBase64(contents)
            } else {
                null
            }
            DatabaseConnector.VanillaInventory.setBase64(uuidPlayer, base64)
            contentsCache = contents
        }
    }

    private val playerDataMap = mutableMapOf<UUIDPlayer, PlayerData>()

    private val OfflinePlayer.vanillaInventory
        get() = UUIDPlayer(this).vanillaInventory

    private val UUIDPlayer.vanillaInventory
        get() = playerDataMap.getOrPut(this) { PlayerData(this) }

    fun load(player: Player) {
        val playerData = player.vanillaInventory
        val contents = playerData.load() ?: return
        player.inventory.contents = contents
    }

    fun save(player: Player) {
        player.vanillaInventory.save(player.inventory)
    }

    fun isEnableGameMode(gameMode: GameMode): Boolean {
        return gameMode in listOf(GameMode.CREATIVE, GameMode.SPECTATOR)
    }
}