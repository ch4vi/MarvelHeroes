package com.xavi.marvelheroes.di

import com.xavi.marvelheroes.data.api.RetrofitConfiguration.Companion.CLIENT
import com.xavi.marvelheroes.data.datasource.CharacterDetailRepositoryImp
import com.xavi.marvelheroes.data.mapper.CharactersMapper
import com.xavi.marvelheroes.domain.repository.CharacterDetailRepository
import com.xavi.marvelheroes.domain.usecase.GetCharacterDetail
import com.xavi.marvelheroes.presentation.CharacterDetailViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val characterDetailModule = module {

    single<CharacterDetailRepository> {
        CharacterDetailRepositoryImp(
            client = get(named(CLIENT)),
            mapper = get(named(CharactersMapper.NAME)),
        )
    }

    single { GetCharacterDetail(repository = get()) }

    viewModel { CharacterDetailViewModel(getCharacterDetail = get()) }
}
