package me.syari.ss.item.vanilla

import me.syari.ss.core.Main.Companion.console
import me.syari.ss.core.config.CreateConfig.config
import me.syari.ss.core.config.CustomConfig
import me.syari.ss.core.config.dataType.ConfigDataType
import me.syari.ss.core.item.InventoryBase64
import me.syari.ss.core.player.UUIDPlayer
import me.syari.ss.item.Main.Companion.itemPlugin
import org.bukkit.GameMode
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory

object VanillaInventory {
    internal data class PlayerData(val uuidPlayer: UUIDPlayer) {
        private var nullableConfig: CustomConfig? = null
        private var contentsCache: Array<ItemStack?>? = null

        private fun getConfigOrCreate(): CustomConfig {
            return nullableConfig ?: {
                config(itemPlugin, console, "VanillaInventory/${uuidPlayer}.yml").apply {
                    nullableConfig = this
                }
            }.invoke()
        }

        fun load(): Array<ItemStack?>? {
            return if (contentsCache == null) {
                val base64 = getConfigOrCreate().get("base64", ConfigDataType.STRING, false) ?: return null
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
            if (contents.filterNotNull().isNotEmpty()) {
                val base64 = InventoryBase64.toBase64(contents)
                getConfigOrCreate().set("base64", base64, true)
            } else {
                nullableConfig?.let {
                    it.delete()
                    nullableConfig = null
                }
            }
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