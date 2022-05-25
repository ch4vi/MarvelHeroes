package com.xavi.marvelheroes.domain.usecase

import androidx.paging.PagingData
import com.xavi.marvelheroes.domain.model.CharacterDomainModel
import com.xavi.marvelheroes.domain.repository.CharacterRepository
import com.xavi.marvelheroes.domain.utils.UseCase
import kotlinx.coroutines.flow.Flow

class GetCharacterList(
    private val repository: CharacterRepository
) : UseCase<PagingData<CharacterDomainModel>, Unit>() {

    override fun run(params: Unit): Flow<PagingData<CharacterDomainModel>> =
        repository.getCharacterList()
}
