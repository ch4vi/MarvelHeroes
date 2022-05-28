package com.xavi.marvelheroes.di

import com.xavi.marvelheroes.data.api.RetrofitConfiguration.Companion.CLIENT
import com.xavi.marvelheroes.data.datasource.CharacterRepositoryImp
import com.xavi.marvelheroes.data.db.AppDatabase
import com.xavi.marvelheroes.data.mapper.CharacterDBMapper
import com.xavi.marvelheroes.data.mapper.CharactersMapper
import com.xavi.marvelheroes.domain.repository.CharacterRepository
import com.xavi.marvelheroes.domain.usecase.GetCharacterList
import com.xavi.marvelheroes.domain.usecase.SearchCharacterList
import com.xavi.marvelheroes.presentation.CharacterListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val characterListModule = module {

    single<CharacterRepository> {
        CharacterRepositoryImp(
            client = get(named(CLIENT)),
            db = get(named(AppDatabase.NAME)),
            apiMapper = get(named(CharactersMapper.NAME)),
            dbMapper = get(named(CharacterDBMapper.NAME)),
        )
    }

    single { GetCharacterList(repository = get()) }
    single { SearchCharacterList(repository = get()) }

    viewModel {
        CharacterListViewModel(
            getCharacterList = get(),
            searchCharacterList = get()
        )
    }
}
