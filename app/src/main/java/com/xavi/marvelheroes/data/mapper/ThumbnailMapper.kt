package com.xavi.marvelheroes.data.mapper

import com.xavi.marvelheroes.data.model.ThumbnailDTO
import com.xavi.marvelheroes.domain.utils.Mapper

class ThumbnailMapper : Mapper<String, ThumbnailDTO> {

    companion object {
        const val NAME = "thumbnail_mapper"
        private const val HTTP = "http:"
        private const val HTTPS = "https:"
    }

    override fun map(dto: ThumbnailDTO): String {
        var path = dto.path ?: throw IllegalArgumentException("path")
        val extension = dto.extension ?: throw IllegalArgumentException("extension")
        if (path.startsWith(HTTP)) path = path.replace(HTTP, HTTPS)

        return "$path.$extension"
    }
}
