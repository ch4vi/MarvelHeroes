package com.xavi.marvelheroes.data.datasource

import com.xavi.marvelheroes.data.api.CharacterDetailService
import com.xavi.marvelheroes.data.api.CharacterListService
import com.xavi.marvelheroes.data.api.RetrofitDataSource
import com.xavi.marvelheroes.data.api.RetrofitPagedPredicate
import com.xavi.marvelheroes.data.api.RetrofitRepository
import com.xavi.marvelheroes.data.model.CharacterDTO
import com.xavi.marvelheroes.data.model.MarvelResponseDTO
import com.xavi.marvelheroes.domain.model.CharacterDomainModel
import com.xavi.marvelheroes.domain.model.CharactersDomainModel
import com.xavi.marvelheroes.domain.model.Failure
import com.xavi.marvelheroes.domain.repository.CharacterDetailRepository
import com.xavi.marvelheroes.domain.utils.Mapper
import com.xavi.marvelheroes.domain.utils.NetworkClient
import com.xavi.marvelheroes.domain.utils.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Retrofit

// region Repository

class CharacterDetailRepositoryImp(
    private val client: NetworkClient<Retrofit>,
    private val mapper: Mapper<CharactersDomainModel, MarvelResponseDTO<CharacterDTO>>,
) : CharacterDetailRepository,
    RetrofitRepository<CharacterListService, CharactersDomainModel, MarvelResponseDTO<CharacterDTO>> {


    override fun getCharacter(characterId: String): Flow<CharacterDomainModel> = flow {
        val dataSource = CharacterDetailDataSource(client, characterId, mapper)
        when (val result = dataSource.load()) {
            is State.Error -> throw result.error
            is State.Success -> {
                val character = result.data.characters.firstOrNull()
                if (character == null) throw Failure.NotFound
                else emit(character)
            }
        }

    }
}

// endregion

// region DataSource

class CharacterDetailDataSource(
    client: NetworkClient<Retrofit>,
    private val characterId: String,
    private val mapper: Mapper<CharactersDomainModel, MarvelResponseDTO<CharacterDTO>>,
) : RetrofitDataSource<
        CharacterDetailService,
        CharactersDomainModel,
        MarvelResponseDTO<CharacterDTO>> {

    override val networkClient = client

    suspend fun load(): State<CharactersDomainModel> {
        val predicate = CharacterDetailPredicate(characterId, mapper)
        return fetch(predicate)
    }
}

// endregion

// region Predicate

class CharacterDetailPredicate(
    private val id: String,
    private val mapper: Mapper<CharactersDomainModel, MarvelResponseDTO<CharacterDTO>>
) : RetrofitPagedPredicate<CharacterDetailService, CharactersDomainModel, MarvelResponseDTO<CharacterDTO>> {

    override fun mapper() = mapper
    override fun service() = CharacterDetailService::class.java
    override fun endpoint(): suspend (CharacterDetailService) -> MarvelResponseDTO<CharacterDTO> = {
        val auth = Auth()
        it.characterDetail(
            id = id,
            ts = auth.ts,
            hash = auth.hash,
            apikey = auth.apikey,
        )
    }
}

// endregion
