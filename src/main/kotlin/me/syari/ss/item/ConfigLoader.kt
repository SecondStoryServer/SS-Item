package me.syari.ss.item

import me.syari.ss.core.Main
import me.syari.ss.core.auto.OnEnable
import me.syari.ss.core.config.CreateConfig.config
import me.syari.ss.core.config.dataType.ConfigDataType
import me.syari.ss.item.Main.Companion.itemPlugin
import org.bukkit.command.CommandSender

object ConfigLoader: OnEnable {
    override fun onEnable() {
        loadConfig(Main.console)
    }

    private val defaultConfig = mapOf(
        "sql.host" to "localhost", "sql.port" to 3306, "sql.database" to "", "sql.user" to "", "sql.password" to ""
    )

    fun loadConfig(output: CommandSender) {
        config(itemPlugin, output, "config.yml", default = defaultConfig) {
            DatabaseConnector.sql = get("sql", ConfigDataType.MYSQL)
        }
    }
}