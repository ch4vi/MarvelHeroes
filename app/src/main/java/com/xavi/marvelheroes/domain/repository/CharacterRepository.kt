package com.xavi.marvelheroes.domain.repository

import com.xavi.marvelheroes.domain.model.CharactersDomainModel
import com.xavi.marvelheroes.domain.utils.State

interface CharacterRepository {
    suspend fun getCharacterList(): State<CharactersDomainModel>
}
