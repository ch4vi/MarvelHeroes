package com.xavi.marvelheroes.data.mapper

import com.xavi.marvelheroes.data.model.ThumbnailDTO
import com.xavi.marvelheroes.domain.model.ThumbnailDomainModel
import com.xavi.marvelheroes.domain.utils.Mapper

class ThumbnailMapper : Mapper<ThumbnailDomainModel, ThumbnailDTO> {

    companion object {
        const val NAME = "thumbnail_mapper"
        private const val HTTP = "http:"
        private const val HTTPS = "https:"
    }

    override fun map(dto: ThumbnailDTO): ThumbnailDomainModel {
        var path = dto.path ?: throw IllegalArgumentException("path")
        val extension = dto.extension ?: throw IllegalArgumentException("extension")
        if (path.startsWith(HTTP)) path = path.replace(HTTP, HTTPS)

        return ThumbnailDomainModel(url = "$path.$extension")
    }
}
