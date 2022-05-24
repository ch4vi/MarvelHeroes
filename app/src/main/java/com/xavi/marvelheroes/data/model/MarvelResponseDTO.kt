package com.xavi.marvelheroes.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.xavi.marvelheroes.domain.utils.DTO

@JsonClass(generateAdapter = true)
data class MarvelResponseDTO<T>(
    @Json(name = "code") val code: Int?,
    @Json(name = "status") val status: String?,
    @Json(name = "copyright") val copyright: String?,
    @Json(name = "attributionText") val attributionText: String?,
    @Json(name = "data") val data: PageDTO<T>?
) : DTO
