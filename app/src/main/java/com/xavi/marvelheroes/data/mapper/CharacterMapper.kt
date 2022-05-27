package com.xavi.marvelheroes.data.mapper

import com.xavi.marvelheroes.data.model.CharacterDTO
import com.xavi.marvelheroes.data.model.ThumbnailDTO
import com.xavi.marvelheroes.domain.model.CharacterDomainModel
import com.xavi.marvelheroes.domain.model.ThumbnailDomainModel
import com.xavi.marvelheroes.domain.utils.Mapper

class CharacterMapper(
    private val thumbnailMapper: Mapper<ThumbnailDomainModel, ThumbnailDTO>
) : Mapper<CharacterDomainModel, CharacterDTO> {

    companion object {
        const val NAME = "character_mapper"
    }

    override fun map(dto: CharacterDTO): CharacterDomainModel {
        val id = dto.id ?: throw IllegalArgumentException("id")
        val thumbnail = mapThumbnail(dto.thumbnail)
        return CharacterDomainModel(
            id = id,
            name = dto.name,
            description = dto.description,
            thumbnail = thumbnail
        )
    }

    @Suppress("SwallowedException")
    private fun mapThumbnail(dto: ThumbnailDTO?): ThumbnailDomainModel? {
        dto ?: return null
        return try {
            thumbnailMapper.map(dto)
        } catch (e: IllegalArgumentException) {
            null
        }
    }
}
