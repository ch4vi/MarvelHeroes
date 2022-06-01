package com.xavi.marvelheroes.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PageDomainModel(
    val offset: Int,
    val limit: Int,
    val total: Int,
    val count: Int
) : Parcelable
