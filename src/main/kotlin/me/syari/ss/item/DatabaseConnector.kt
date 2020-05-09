package me.syari.ss.item

import me.syari.ss.core.player.UUIDPlayer
import me.syari.ss.core.sql.MySQL
import me.syari.ss.item.chest.ItemChest
import me.syari.ss.item.compass.CompassItem
import me.syari.ss.item.equip.EquipItem
import me.syari.ss.item.general.GeneralItem
import java.sql.Statement

object DatabaseConnector {
    internal var sql: MySQL? = null

    fun createTable() {
        sql?.use {
            Chest.MaxPage.createTable(this)
            Chest.General.createTable(this)
            Chest.Equip.createTable(this)
            Chest.Compass.createTable(this)
        }
    }

    object Chest {
        object MaxPage {
            private val maxPageCache = mutableMapOf<UUIDPlayer, Int?>()

            fun createTable(statement: Statement) {
                statement.executeUpdate(
                    """
                        CREATE TABLE IF NOT EXISTS MaxPage(
                            UUID VARCHAR(36),
                            ChestName VARCHAR(255),
                            Size TINYINT UNSIGNED,
                            PRIMARY KEY (UUID, ChestName)
                        );
                    """.trimIndent()
                )
            }

            fun get(uuidPlayer: UUIDPlayer, chest: ItemChest): Int? {
                return maxPageCache.getOrPut(uuidPlayer) { getFromSQL(uuidPlayer, chest) }
            }

            private fun getFromSQL(uuidPlayer: UUIDPlayer, chest: ItemChest): Int? {
                var maxPage: Int? = null
                sql?.use {
                    val result = executeQuery(
                        """
                            SELECT Size FROM MaxPage
                                WHERE
                                    UUID = '$uuidPlayer'
                                AND
                                    ChestName = '${chest.sizeColumnName}'
                            LIMIT 1;
                        """.trimIndent()
                    )
                    if (result.next()) {
                        maxPage = result.getInt(1)
                    }
                }
                return maxPage
            }

            fun set(uuidPlayer: UUIDPlayer, chest: ItemChest, size: Int?) {
                sql?.use {
                    if (size != null) {
                        executeUpdate(
                            """
                                INSERT INTO MaxPage VALUE (
                                    '$uuidPlayer',
                                    '${chest.sizeColumnName}',
                                    $size
                                ) ON DUPLICATE KEY UPDATE Size = $size;
                            """.trimIndent()
                        )
                    } else {
                        executeUpdate(
                            """
                                DELETE FROM MaxPage
                                    WHERE
                                        UUID = '$uuidPlayer'
                                    AND
                                        ChestName = '${chest.sizeColumnName}'
                                LIMIT 1;
                            """.trimIndent()
                        )
                    }
                }
            }
        }

        object General {
            fun createTable(statement: Statement) {
                statement.executeUpdate(
                    """
                        CREATE TABLE IF NOT EXISTS GeneralItemChest(
                            UUID VARCHAR(36),
                            ItemID VARCHAR(255),
                            Amount INT,
                            PRIMARY KEY (UUID, ItemID)
                        );
                    """.trimIndent()
                )
            }

            fun get(uuidPlayer: UUIDPlayer): Map<GeneralItem, Int> {
                val map = mutableMapOf<GeneralItem, Int>()
                sql?.use {
                    val result = executeQuery(
                        """
                            SELECT ItemID, Amount FROM GeneralItemChest WHERE UUID = '$uuidPlayer';
                        """.trimIndent()
                    )
                    while (result.next()) {
                        GeneralItem.from(result.getString(1))?.let { item ->
                            map[item] = result.getInt(2)
                        }
                    }
                }
                return map
            }

            fun set(uuidPlayer: UUIDPlayer, item: GeneralItem, amount: Int) {
                sql?.use {
                    if (0 < amount) {
                        executeUpdate(
                            """
                                INSERT INTO GeneralItemChest VALUE (
                                    '$uuidPlayer',
                                    '${item.id}',
                                    $amount
                                ) ON DUPLICATE KEY UPDATE Amount = $amount;
                            """.trimIndent()
                        )
                    } else {
                        executeUpdate(
                            """
                                DELETE FROM GeneralItemChest
                                    WHERE
                                        UUID = '$uuidPlayer'
                                    AND
                                        ItemID = '${item.id}'
                                LIMIT 1;
                            """.trimIndent()
                        )
                    }
                }
            }
        }

        object Equip {
            fun createTable(statement: Statement) {
                statement.executeUpdate(
                    """
                        CREATE TABLE IF NOT EXISTS EquipItemChest(
                            UUID VARCHAR(36),
                            ItemID VARCHAR(255),
                            Enhance INT,
                            EnhancePlus INT
                        );
                    """.trimIndent()
                )
            }

            fun get(uuidPlayer: UUIDPlayer): List<EquipItem.Data> {
                val list = mutableListOf<EquipItem.Data>()
                sql?.use {
                    val result = executeQuery(
                        """
                            SELECT ItemID, Enhance, EnhancePlus FROM EquipItemChest WHERE UUID = '$uuidPlayer';
                        """.trimIndent()
                    )
                    while (result.next()) {
                        EquipItem.from(result.getString(1))?.let { item ->
                            val enhance = result.getInt(2)
                            val enhancePlus = result.getInt(3)
                            list.add(EquipItem.Data(item, enhance, enhancePlus))
                        }
                    }
                }
                return list
            }

            fun add(uuidPlayer: UUIDPlayer, item: EquipItem.Data) {
                sql?.use {
                    executeUpdate(
                        """
                            INSERT INTO CompassItemChest VALUE (
                                '$uuidPlayer',
                                '${item.equipItem.id}',
                                ${item.enhance},
                                ${item.enhancePlus}
                            );
                        """.trimIndent()
                    )
                }
            }

            fun remove(uuidPlayer: UUIDPlayer, item: EquipItem.Data) {
                sql?.use {
                    executeUpdate(
                        """
                            DELETE FROM CompassItemChest
                                WHERE
                                    UUID = '$uuidPlayer'
                                AND
                                    ItemID = '${item.equipItem.id}'
                                AND
                                    Enhance = ${item.enhance}
                                AND
                                    EnhancePlus = ${item.enhancePlus}
                            LIMIT 1;
                        """.trimIndent()
                    )
                }
            }
        }

        object Compass {
            fun createTable(statement: Statement) {
                statement.executeUpdate(
                    """
                        CREATE TABLE IF NOT EXISTS CompassItemChest(
                            UUID VARCHAR(36),
                            ItemID VARCHAR(255),
                            PRIMARY KEY (UUID, ItemID)
                        );
                    """.trimIndent()
                )
            }

            fun get(uuidPlayer: UUIDPlayer): Set<CompassItem> {
                val list = mutableSetOf<CompassItem>()
                sql?.use {
                    val result = executeQuery(
                        """
                            SELECT ItemID FROM CompassItemChest WHERE UUID = '$uuidPlayer';
                        """.trimIndent()
                    )
                    while (result.next()) {
                        CompassItem.from(result.getString(1))?.let { item ->
                            list.add(item)
                        }
                    }
                }
                return list
            }

            fun add(uuidPlayer: UUIDPlayer, item: CompassItem) {
                sql?.use {
                    executeUpdate(
                        """
                            INSERT INTO CompassItemChest VALUE (
                                '$uuidPlayer',
                                '${item.id}'
                            );
                        """.trimIndent()
                    )
                }
            }

            fun remove(uuidPlayer: UUIDPlayer, item: CompassItem) {
                sql?.use {
                    executeUpdate(
                        """
                            DELETE FROM CompassItemChest
                                WHERE
                                    UUID = '$uuidPlayer'
                                AND
                                    ItemID = '${item.id}'
                            LIMIT 1;
                        """.trimIndent()
                    )
                }
            }
        }
    }
}
