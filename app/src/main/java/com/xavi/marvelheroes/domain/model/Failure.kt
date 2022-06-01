package com.xavi.marvelheroes.domain.model

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.xavi.marvelheroes.domain.utils.State

sealed class Failure(reason: String? = null) : Throwable(reason) {
    object NetworkError : Failure()
    object NotFound : Failure()
    class Unexpected(reason: String? = null) : Failure(reason)
    class MalformedError(reason: String? = null) : Failure(reason)
    class GenericError(reason: String? = null) : Failure(reason)
    class ResponseError(val error: ErrorDomainModel) : Failure()
    object AppendError : Failure()
}

fun Failure.toState() = State.Error(this)

@Keep
@JsonClass(generateAdapter = true)
data class ErrorDomainModel(
    @Json(name = "message") val status: String,
    @Json(name = "code") val code: String,
) {
    enum class ErrorType(val code: String) {
        InvalidCredentials("InvalidCredentials"),
        Unknown("Unknown")
    }

    val type: ErrorType
        get() = getType(code)

    companion object {
        fun getType(code: String) =
            ErrorType.values().firstOrNull { code == it.code } ?: ErrorType.Unknown
    }
}
