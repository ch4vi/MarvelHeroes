package com.xavi.marvelheroes.domain.repository

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.xavi.marvelheroes.domain.model.CharacterDomainModel
import com.xavi.marvelheroes.domain.utils.PagedRepository
import kotlinx.coroutines.flow.Flow

interface CharacterListRepository : PagedRepository {

    fun getCharacterList(pagingConfig: PagingConfig = getDefaultPageConfig()): Flow<PagingData<CharacterDomainModel>>

    fun searchCharacterList(
        pagingConfig: PagingConfig = getDefaultPageConfig(),
        queryName: String?
    ): Flow<PagingData<CharacterDomainModel>>
}
