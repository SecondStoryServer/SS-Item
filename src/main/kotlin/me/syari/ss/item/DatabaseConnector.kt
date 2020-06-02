package me.syari.ss.item

import me.syari.ss.core.auto.OnEnable
import me.syari.ss.core.player.UUIDPlayer
import me.syari.ss.core.sql.Database.Companion.asListNotNull
import me.syari.ss.core.sql.Database.Companion.asMapNotNull
import me.syari.ss.core.sql.Database.Companion.asSetNotNull
import me.syari.ss.core.sql.Database.Companion.nextOrNull
import me.syari.ss.core.sql.MySQL
import me.syari.ss.item.chest.ItemChest
import me.syari.ss.item.itemRegister.compass.CompassItem
import me.syari.ss.item.itemRegister.equip.EnhancedEquipItem
import me.syari.ss.item.itemRegister.equip.EquipItem
import me.syari.ss.item.itemRegister.general.GeneralItem
import me.syari.ss.item.itemRegister.general.GeneralItemWithAmount
import java.sql.Statement
import java.util.UUID

object DatabaseConnector: OnEnable {
    override fun onEnable() {
        createTable()
    }

    internal var sql: MySQL? = null

    fun createTable() {
        sql?.use {
            Chest.MaxPage.createTable(this)
            Chest.General.createTable(this)
            Chest.Equip.createTable(this)
            Chest.Compass.createTable(this)
            VanillaInventory.createTable(this)
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
                return sql?.use {
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
                    result.nextOrNull {
                        result.getInt(1)
                    }
                }
            }

            fun set(uuidPlayer: UUIDPlayer, chest: ItemChest, size: Int?) {
                if (size != null) {
                    sql?.use {
                        executeUpdate(
                            """
                                INSERT INTO MaxPage VALUE (
                                    '$uuidPlayer',
                                    '${chest.sizeColumnName}',
                                    $size
                                ) ON DUPLICATE KEY UPDATE Size = $size;
                            """.trimIndent()
                        )
                    }
                } else {
                    delete(uuidPlayer, chest)
                }
            }

            fun delete(uuidPlayer: UUIDPlayer, chest: ItemChest) {
                sql?.use {
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

            fun delete(uuidPlayer: UUIDPlayer) {
                sql?.use {
                    executeUpdate(
                        """
                        DELETE FROM MaxPage WHERE UUID = '$uuidPlayer' LIMIT 1;
                    """.trimIndent()
                    )
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

            fun get(uuidPlayer: UUIDPlayer): Map<GeneralItem, GeneralItemWithAmount> {
                val map = sql?.use {
                    val result = executeQuery(
                        """
                            SELECT ItemID, Amount FROM GeneralItemChest WHERE UUID = '$uuidPlayer';
                        """.trimIndent()
                    )
                    result.asMapNotNull {
                        val item = GeneralItem.from(result.getString(1))
                        if (item != null) {
                            item to result.getInt(2)
                        } else {
                            null
                        }
                    }
                } ?: emptyMap()
                return GeneralItemWithAmount.from(map)
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

            fun delete(uuidPlayer: UUIDPlayer) {
                sql?.use {
                    executeUpdate(
                        """
                        DELETE FROM GeneralItemChest WHERE UUID = '$uuidPlayer' LIMIT 1;
                    """.trimIndent()
                    )
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
                            ItemUUID VARCHAR(36),
                            Enhance INT
                        );
                    """.trimIndent()
                )
            }

            fun get(uuidPlayer: UUIDPlayer): List<EnhancedEquipItem> {
                return sql?.use {
                    val result = executeQuery(
                        """
                            SELECT ItemID, ItemUUID, Enhance FROM EquipItemChest WHERE UUID = '$uuidPlayer';
                        """.trimIndent()
                    )
                    result.asListNotNull {
                        EquipItem.from(result.getString(1))?.let { item ->
                            val uuid = UUID.fromString(result.getString(2))
                            val enhance = result.getInt(3)
                            item.getEnhanced(uuid, enhance)
                        }
                    }
                } ?: emptyList()
            }

            fun add(uuidPlayer: UUIDPlayer, item: EnhancedEquipItem) {
                sql?.use {
                    executeUpdate(
                        """
                            INSERT INTO CompassItemChest VALUE (
                                '$uuidPlayer',
                                '${item.data.id}',
                                '${item.uuid}',
                                ${item.enhance}
                            );
                        """.trimIndent()
                    )
                }
            }

            fun remove(uuidPlayer: UUIDPlayer, item: EnhancedEquipItem) {
                sql?.use {
                    executeUpdate(
                        """
                            DELETE FROM CompassItemChest
                                WHERE
                                    UUID = '$uuidPlayer'
                                AND
                                    ItemUUID = '${item.uuid}'
                            LIMIT 1;
                        """.trimIndent()
                    )
                }
            }

            fun delete(uuidPlayer: UUIDPlayer) {
                sql?.use {
                    executeUpdate(
                        """
                        DELETE FROM CompassItemChest WHERE UUID = '$uuidPlayer';
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
                return sql?.use {
                    val result = executeQuery(
                        """
                            SELECT ItemID FROM CompassItemChest WHERE UUID = '$uuidPlayer';
                        """.trimIndent()
                    )
                    result.asSetNotNull {
                        CompassItem.from(result.getString(1))
                    }
                } ?: emptySet()
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

            fun delete(uuidPlayer: UUIDPlayer) {
                sql?.use {
                    executeUpdate(
                        """
                        DELETE FROM CompassItemChest WHERE UUID = '$uuidPlayer' LIMIT 1;
                    """.trimIndent()
                    )
                }
            }
        }
    }

    object VanillaInventory {
        fun createTable(statement: Statement) {
            statement.executeUpdate(
                """
                    CREATE TABLE IF NOT EXISTS VanillaInventory(
                        UUID VARCHAR(36) PRIMARY KEY,
                        Base64 LONGTEXT
                    );
                """.trimIndent()
            )
        }

        fun getBase64(uuidPlayer: UUIDPlayer): String? {
            return sql?.use {
                val result = executeQuery(
                    """
                        SELECT Base64 FROM VanillaInventory WHERE UUID = '$uuidPlayer' LIMIT 1;
                    """.trimIndent()
                )
                result.nextOrNull {
                    getString(1)
                }
            }
        }

        fun setBase64(uuidPlayer: UUIDPlayer, base64: String?) {
            if (base64 != null) {
                sql?.use {
                    executeUpdate(
                        """
                            INSERT INTO VanillaInventory VALUE (
                                '$uuidPlayer',
                                '$base64
                            ) ON DUPLICATE KEY UPDATE Base64 = $base64;
                        """.trimIndent()
                    )
                }
            } else {
                delete(uuidPlayer)
            }
        }

        fun delete(uuidPlayer: UUIDPlayer) {
            sql?.use {
                executeUpdate(
                    """
                        DELETE FROM VanillaInventory WHERE UUID = '$uuidPlayer' LIMIT 1;
                    """.trimIndent()
                )
            }
        }
    }
}
