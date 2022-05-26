package com.xavi.marvelheroes.ui

import android.content.Context
import androidx.annotation.StringRes
import com.xavi.marvelheroes.R
import com.xavi.marvelheroes.domain.model.ErrorDomainModel
import com.xavi.marvelheroes.domain.model.Failure

fun Throwable.getMessage(context: Context): String {
    return when (this) {
        is Failure.ResponseError -> context.getString(this.getMessageRes())
        else -> this.message ?: context.getString(R.string.app_error_unknown)
    }
}

@StringRes
fun Failure.ResponseError.getMessageRes(): Int {
    return when (this.error.type) {
        ErrorDomainModel.ErrorType.InvalidCredentials -> R.string.app_error_invalid_request
        ErrorDomainModel.ErrorType.Unknown -> R.string.app_error_unknown
    }
}