package com.xavi.marvelheroes.domain.model

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

sealed class Failure(val reason: String? = null) : Throwable(reason) {
    object NetworkError : Failure()
    class Unexpected(reason: String? = null) : Failure(reason)
    class MalformedError(reason: String? = null) : Failure(reason)
    class GenericError(reason: String? = null) : Failure(reason)
    class ResponseError(val error: ErrorDomainModel) : Failure()
    object UnauthorizedError : Failure()
}

@Keep
@JsonClass(generateAdapter = true)
data class ErrorDomainModel(
    @Json(name = "message") val status: String,
    @Json(name = "code") val code: String,
)
