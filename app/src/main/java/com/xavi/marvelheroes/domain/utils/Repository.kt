package com.xavi.marvelheroes.domain.utils

import androidx.paging.PagingConfig
import com.xavi.marvelheroes.data.datasource.PageDefault.DEFAULT_LIMIT

interface Repository<T, R, P> where
R : DTO,
P : Predicate<T, R>

interface PagedRepository {
    fun getDefaultPageConfig() =
        PagingConfig(pageSize = DEFAULT_LIMIT, enablePlaceholders = true)
}
