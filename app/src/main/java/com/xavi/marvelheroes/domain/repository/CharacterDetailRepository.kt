package com.xavi.marvelheroes.domain.repository

import com.xavi.marvelheroes.domain.model.CharacterDomainModel
import com.xavi.marvelheroes.domain.utils.PagedRepository
import kotlinx.coroutines.flow.Flow

interface CharacterDetailRepository : PagedRepository {
    fun getCharacter(characterId: String): Flow<CharacterDomainModel>
}