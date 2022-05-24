package com.xavi.marvelheroes.domain.model

data class PageDomainModel(
    val offset: Int,
    val limit: Int,
    val total: Int,
    val count: Int
)
