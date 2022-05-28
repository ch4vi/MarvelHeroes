package com.xavi.marvelheroes.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CharacterDB::class, PageDB::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        const val NAME = "appDB"
    }

    abstract fun characterDao(): CharacterDao
    abstract fun pageDao(): PageDao
}
