package com.xavi.marvelheroes.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.xavi.marvelheroes.domain.utils.DTO

@JsonClass(generateAdapter = true)
data class PageDTO<T>(
    @Json(name = "offset") val offset: Int?,
    @Json(name = "limit") val limit: Int?,
    @Json(name = "total") val total: Int?,
    @Json(name = "count") val count: Int?,
    @Json(name = "results") val results: List<T>?
) : DTO
