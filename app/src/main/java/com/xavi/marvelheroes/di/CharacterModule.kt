package com.xavi.marvelheroes.di

import com.xavi.marvelheroes.data.api.RetrofitConfiguration.Companion.CLIENT
import com.xavi.marvelheroes.data.datasource.CharacterRepositoryImp
import com.xavi.marvelheroes.data.mapper.CharactersMapper
import com.xavi.marvelheroes.domain.repository.CharacterRepository
import com.xavi.marvelheroes.domain.usecase.GetCharacterList
import com.xavi.marvelheroes.presentation.CharacterListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val characterListModule = module {

    single<CharacterRepository> {
        CharacterRepositoryImp(
            client = get(named(CLIENT)),
            mapper = get(named(CharactersMapper.NAME))
        )
    }

    single { GetCharacterList(repository = get()) }

    viewModel { CharacterListViewModel(getCharacterList = get()) }
}
