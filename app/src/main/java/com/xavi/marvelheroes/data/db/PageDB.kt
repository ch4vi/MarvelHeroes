package com.xavi.marvelheroes.data.db

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(tableName = "page_cache")
data class PageDB(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "previous") val previous: Int?,
    @ColumnInfo(name = "next") val next: Int?
)

@Dao
interface PageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<PageDB>)

    @Query("SELECT * FROM page_cache WHERE id = :id")
    suspend fun getById(id: String): PageDB?

    @Query("DELETE FROM page_cache")
    suspend fun clearAll()
}
