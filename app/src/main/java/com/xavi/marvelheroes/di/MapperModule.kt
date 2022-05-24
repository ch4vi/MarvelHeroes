package com.xavi.marvelheroes.di

import com.xavi.marvelheroes.data.datasource.CharacterMapper
import com.xavi.marvelheroes.data.datasource.CharactersMapper
import com.xavi.marvelheroes.data.datasource.ThumbnailMapper
import com.xavi.marvelheroes.data.model.CharacterDTO
import com.xavi.marvelheroes.data.model.MarvelResponseDTO
import com.xavi.marvelheroes.data.model.ThumbnailDTO
import com.xavi.marvelheroes.domain.model.CharacterDomainModel
import com.xavi.marvelheroes.domain.model.CharactersDomainModel
import com.xavi.marvelheroes.domain.model.ThumbnailDomainModel
import com.xavi.marvelheroes.domain.utils.Mapper
import org.koin.core.qualifier.named
import org.koin.dsl.module

val mapperModule = module {
    single<Mapper<ThumbnailDomainModel, ThumbnailDTO>>(named(ThumbnailMapper.NAME)) { ThumbnailMapper() }
    single<Mapper<CharacterDomainModel, CharacterDTO>>(named(CharacterMapper.NAME)) {
        CharacterMapper(
            thumbnailMapper = get(named(ThumbnailMapper.NAME))
        )
    }
    single<Mapper<CharactersDomainModel, MarvelResponseDTO<CharacterDTO>>>(named(CharactersMapper.NAME)) {
        CharactersMapper(
            characterMapper = get(named(CharacterMapper.NAME))
        )
    }
}
