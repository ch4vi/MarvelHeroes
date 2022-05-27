package com.xavi.marvelheroes.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PageDomainModel(
    val offset: Int,
    val limit: Int,
    val total: Int,
    val count: Int
) : Parcelable {
    companion object PaginationDefault {
        const val LIMIT = 20
        const val OFFSET = 0
        fun start(limit: Int = LIMIT) = PageDomainModel(OFFSET, limit, 0, 0)
    }
}
