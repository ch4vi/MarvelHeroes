package com.xavi.marvelheroes.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.xavi.marvelheroes.domain.utils.DTO

@JsonClass(generateAdapter = true)
data class CharacterDTO(
    @Json(name = "id") val id: String?,
    @Json(name = "name") val name: String?,
    @Json(name = "thumbnail") val thumbnail: ThumbnailDTO?
) : DTO
