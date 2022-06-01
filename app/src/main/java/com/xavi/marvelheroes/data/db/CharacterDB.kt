package com.xavi.marvelheroes.data.db

import androidx.paging.PagingSource
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(tableName = "character_cache")
data class CharacterDB(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "thumbnail") val thumbnail: String?
)

@Dao
interface CharacterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<CharacterDB>)

    @Query("SELECT * FROM character_cache")
    fun getAll(): PagingSource<Int, CharacterDB>

    @Query("DELETE FROM character_cache")
    suspend fun clearAll()
}
