package com.xavi.marvelheroes.domain.usecase

import com.xavi.marvelheroes.domain.model.CharacterDomainModel
import com.xavi.marvelheroes.domain.repository.CharacterDetailRepository
import com.xavi.marvelheroes.domain.utils.UseCase
import kotlinx.coroutines.flow.Flow

class GetCharacterDetail(
    private val repository: CharacterDetailRepository
) : UseCase<CharacterDomainModel, GetCharacterDetail.Param>() {

    override fun run(params: Param): Flow<CharacterDomainModel> =
        repository.getCharacter(params.id)

    class Param(val id: String)
}
