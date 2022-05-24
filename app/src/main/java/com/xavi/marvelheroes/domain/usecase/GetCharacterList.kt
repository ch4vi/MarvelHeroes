package com.xavi.marvelheroes.domain.usecase

import com.xavi.marvelheroes.domain.model.CharactersDomainModel
import com.xavi.marvelheroes.domain.repository.CharacterRepository
import com.xavi.marvelheroes.domain.utils.State
import com.xavi.marvelheroes.domain.utils.UseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetCharacterList(
    private val repository: CharacterRepository
) : UseCase<CharactersDomainModel, Unit>() {

    override fun run(params: Unit): Flow<CharactersDomainModel> = flow {
        when (val result = repository.getCharacterList()) {
            is State.Success -> emit(result.data)
            is State.Error -> throw result.error
        }
    }
}
