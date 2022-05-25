package com.xavi.marvelheroes.data.mapper

import com.xavi.marvelheroes.data.model.CharacterDTO
import com.xavi.marvelheroes.data.model.MarvelResponseDTO
import com.xavi.marvelheroes.data.model.PageDTO
import com.xavi.marvelheroes.data.model.PageMapper
import com.xavi.marvelheroes.domain.model.CharacterDomainModel
import com.xavi.marvelheroes.domain.model.CharactersDomainModel
import com.xavi.marvelheroes.domain.model.PageDomainModel
import com.xavi.marvelheroes.domain.utils.Mapper

class CharactersMapper(
    private val characterMapper: Mapper<CharacterDomainModel, CharacterDTO>
) : Mapper<CharactersDomainModel, MarvelResponseDTO<CharacterDTO>> {

    companion object {
        const val NAME = "characters_mapper"
    }

    private val pageMapper: Mapper<PageDomainModel, PageDTO<CharacterDTO>> = PageMapper()

    override fun map(dto: MarvelResponseDTO<CharacterDTO>): CharactersDomainModel {
        val pageDto = dto.data ?: throw java.lang.IllegalArgumentException("page")
        val page = pageMapper.map(pageDto)
        val characters = pageDto.results?.map { characterMapper.map(it) } ?: listOf()
        return CharactersDomainModel(page, characters)
    }
}