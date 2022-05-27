package com.xavi.marvelheroes.domain.utils

import androidx.paging.PagingConfig
import com.xavi.marvelheroes.domain.model.PageDomainModel

interface Repository<T, R, P> where
R : DTO,
P : Predicate<T, R>

interface PagedRepository {
    fun getDefaultPageConfig() =
        PagingConfig(pageSize = PageDomainModel.LIMIT, enablePlaceholders = true)
}
