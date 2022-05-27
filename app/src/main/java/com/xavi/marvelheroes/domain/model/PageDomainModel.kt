package com.xavi.marvelheroes.domain.model

data class PageDomainModel(
    val offset: Int,
    val limit: Int,
    val total: Int,
    val count: Int
) {
    companion object PaginationDefault {
        const val LIMIT = 20
        const val OFFSET = 0
        fun start(limit: Int = LIMIT) = PageDomainModel(OFFSET, limit, 0, 0)
    }
}
