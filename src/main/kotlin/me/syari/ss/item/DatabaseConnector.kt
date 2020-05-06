package me.syari.ss.item

import me.syari.ss.core.player.UUIDPlayer
import me.syari.ss.core.sql.MySQL
import me.syari.ss.item.chest.ItemChest
import me.syari.ss.item.general.GeneralItem
import java.sql.Statement

object DatabaseConnector {
    internal var sql: MySQL? = null

    fun createTable(){
        sql?.use {
            Chest.MaxPage.createTable(this)
            Chest.General.createTable(this)
        }
    }

    object Chest {
        object MaxPage {
            private val maxPageCache = mutableMapOf<UUIDPlayer, Int?>()

            fun createTable(statement: Statement){
                statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS MaxPage(
                        UUID VARCHAR(36),
                        ChestName VARCHAR(255),
                        Size TINYINT UNSIGNED,
                        PRIMARY KEY (UUID, ChestName)
                    );
                """.trimIndent())
            }

            fun get(uuidPlayer: UUIDPlayer, chest: ItemChest): Int? {
                return maxPageCache.getOrPut(uuidPlayer){ getFromSQL(uuidPlayer, chest) }
            }

            private fun getFromSQL(uuidPlayer: UUIDPlayer, chest: ItemChest): Int? {
                var maxPage: Int? = null
                sql?.use {
                    val result = executeQuery("""
                        SELECT Size FROM MaxPage
                            WHERE
                                UUID = '$uuidPlayer'
                            AND
                                ChestName = '${chest.sizeColumnName}'
                        LIMIT 1;
                    """.trimIndent())
                    if(result.next()){
                        maxPage = result.getInt(1)
                    }
                }
                return maxPage
            }

            fun set(uuidPlayer: UUIDPlayer, chest: ItemChest, size: Int?) {
                sql?.use {
                    if(size != null){
                        executeUpdate("""
                            INSERT INTO MaxPage VALUE (
                                '$uuidPlayer',
                                '${chest.sizeColumnName}',
                                $size
                            ) ON DUPLICATE KEY UPDATE Size = $size;
                        """.trimIndent())
                    } else {
                        executeUpdate("""
                            DELETE FROM MaxPage
                                WHERE
                                    UUID = '$uuidPlayer'
                                AND
                                    ChestName = '${chest.sizeColumnName}'
                            LIMIT 1;
                        """.trimIndent())
                    }
                }
            }
        }

        object General {
            fun createTable(statement: Statement){
                statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS GeneralItemChest(
                        UUID VARCHAR(36),
                        ItemID VARCHAR(255),
                        Amount INT,
                        PRIMARY KEY (UUID, ItemID)
                    );
                """.trimIndent())
            }

            fun get(uuidPlayer: UUIDPlayer): Map<GeneralItem, Int> {
                val map = mutableMapOf<GeneralItem, Int>()
                sql?.use {
                    val result = executeQuery("""
                        SELECT ItemID, Amount FROM GeneralItemChest WHERE UUID = '$uuidPlayer';
                    """.trimIndent())
                    while(result.next()){
                       GeneralItem.from(result.getString(1))?.let { item ->
                           map[item] = result.getInt(2)
                        }
                    }
                }
                return map
            }
        }
    }
}
