package com.xavi.marvelheroes.di

import com.xavi.marvelheroes.data.api.RetrofitConfiguration.Companion.CLIENT
import com.xavi.marvelheroes.data.datasource.CharacterListDataSource
import com.xavi.marvelheroes.data.datasource.CharacterListDataSourceImpl
import com.xavi.marvelheroes.data.datasource.CharacterRepositoryImp
import com.xavi.marvelheroes.data.datasource.CharactersMapper
import com.xavi.marvelheroes.domain.repository.CharacterRepository
import com.xavi.marvelheroes.domain.usecase.GetCharacterList
import com.xavi.marvelheroes.presentation.TestViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val characterListModule = module {
    single<CharacterListDataSource> { CharacterListDataSourceImpl(client = get(named(CLIENT))) }

    single<CharacterRepository> {
        CharacterRepositoryImp(
            dataSource = get(),
            mapper = get(named(CharactersMapper.NAME))
        )
    }

    single { GetCharacterList(repository = get()) }

    viewModel { TestViewModel(getCharacterList = get()) }
}