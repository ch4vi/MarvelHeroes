package com.xavi.marvelheroes.data.db

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(tableName = "last_update_cache")
data class LastUpdateDB(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Int = FIRST,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
) {
    companion object {
        const val FIRST = 1
    }
}

@Dao
interface LastUpdateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(lastUpdate: LastUpdateDB)

    @Query("SELECT * FROM last_update_cache WHERE id = :id")
    suspend fun get(id: Int = LastUpdateDB.FIRST): LastUpdateDB?

    @Query("DELETE FROM last_update_cache")
    suspend fun clearAll()
}
