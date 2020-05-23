package me.syari.ss.item

import me.syari.ss.battle.equipment.ElementType
import me.syari.ss.core.auto.Event
import me.syari.ss.core.auto.OnEnable
import me.syari.ss.core.command.create.CreateCommand.createCommand
import me.syari.ss.core.command.create.ErrorMessage
import me.syari.ss.core.item.ItemStackPlus.give
import me.syari.ss.item.compass.CompassItem
import me.syari.ss.item.custom.register.ItemRegister
import me.syari.ss.item.custom.register.RegisterFunction
import me.syari.ss.item.equip.EquipItem
import me.syari.ss.item.equip.weapon.EnhancedWeaponItem
import me.syari.ss.item.equip.weapon.WeaponItem
import me.syari.ss.item.equip.weapon.WeaponType
import me.syari.ss.item.general.GeneralItem
import me.syari.ss.item.general.potion.HealPotion
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class Main: JavaPlugin() {
    companion object {
        internal lateinit var itemPlugin: JavaPlugin
    }

    override fun onEnable() {
        itemPlugin = this
        ItemRegister.add(
            GeneralItem, EquipItem, CompassItem
        )
        RegisterFunction.add(
            HealPotion
        )
        OnEnable.register(
            ConfigLoader, DatabaseConnector
        )
        Event.register(
            this, EventListener
        )
        testCommand()
    }

    private fun testCommand() {
        val testWand = WeaponItem.create(
            WeaponType.Wand,
            "test-wand",
            Material.DIAMOND_HOE,
            "&bテストワンド",
            "テスト用に作成された杖",
            ItemRarity.SuperRare,
            ElementType.Dark,
            10F,
            0.5F,
            3.0F
        )
        testWand.register()

        createCommand(this, "test-command", "SS-Item-Test") { sender, _ ->
            if (sender !is Player) return@createCommand sendError(ErrorMessage.OnlyPlayer)
            val enhancedTestSword = EnhancedWeaponItem(testWand, 50)
            sender.give(enhancedTestSword.itemStack)
            sendWithPrefix("アイテムを渡したよ")
        }
    }
}