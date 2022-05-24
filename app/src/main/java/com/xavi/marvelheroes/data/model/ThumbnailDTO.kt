package com.xavi.marvelheroes.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.xavi.marvelheroes.domain.utils.DTO

@JsonClass(generateAdapter = true)
data class ThumbnailDTO(
    @Json(name = "path") val path: String?,
    @Json(name = "extension") val extension: String?
) : DTO
