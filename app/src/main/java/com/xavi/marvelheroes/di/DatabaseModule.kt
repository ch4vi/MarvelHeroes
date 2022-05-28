package com.xavi.marvelheroes.di

import android.content.Context
import androidx.room.Room
import com.xavi.marvelheroes.data.db.AppDatabase
import com.xavi.marvelheroes.data.db.CharacterDB
import com.xavi.marvelheroes.data.mapper.CharacterDBMapper
import com.xavi.marvelheroes.domain.model.CharacterDomainModel
import com.xavi.marvelheroes.domain.utils.DBMapper
import org.koin.android.ext.koin.androidApplication
import org.koin.core.qualifier.named
import org.koin.dsl.module

val roomModule = module {
    single<DBMapper<CharacterDomainModel, CharacterDB>>(named(CharacterDBMapper.NAME)) { CharacterDBMapper() }

    single(named(AppDatabase.NAME)) { initDatabase(androidApplication()) }
}

fun initDatabase(context: Context): AppDatabase {
    return Room.databaseBuilder(
        context,
        AppDatabase::class.java, "paging-cache"
    ).build()
}
