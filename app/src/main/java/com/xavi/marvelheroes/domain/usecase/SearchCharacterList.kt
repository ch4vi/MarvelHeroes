package com.xavi.marvelheroes.domain.usecase

import androidx.paging.PagingData
import com.xavi.marvelheroes.domain.model.CharacterDomainModel
import com.xavi.marvelheroes.domain.repository.CharacterRepository
import com.xavi.marvelheroes.domain.utils.UseCase
import kotlinx.coroutines.flow.Flow

class SearchCharacterList(
    private val repository: CharacterRepository
) : UseCase<PagingData<CharacterDomainModel>, SearchCharacterList.Param>() {

    override fun run(params: Param): Flow<PagingData<CharacterDomainModel>> =
        repository.searchCharacterList(queryName = params.queryName)

    class Param(val queryName: String?)
}
