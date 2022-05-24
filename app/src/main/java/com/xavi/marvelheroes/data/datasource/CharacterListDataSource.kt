package com.xavi.marvelheroes.data.datasource

import com.xavi.marvelheroes.data.api.CharacterListService
import com.xavi.marvelheroes.data.api.RetrofitDataSource
import com.xavi.marvelheroes.data.api.RetrofitPredicate
import com.xavi.marvelheroes.data.api.RetrofitRepository
import com.xavi.marvelheroes.data.model.CharacterDTO
import com.xavi.marvelheroes.data.model.MarvelResponseDTO
import com.xavi.marvelheroes.data.model.PageDTO
import com.xavi.marvelheroes.data.model.PageMapper
import com.xavi.marvelheroes.data.model.ThumbnailDTO
import com.xavi.marvelheroes.domain.model.CharacterDomainModel
import com.xavi.marvelheroes.domain.model.CharactersDomainModel
import com.xavi.marvelheroes.domain.model.PageDomainModel
import com.xavi.marvelheroes.domain.model.ThumbnailDomainModel
import com.xavi.marvelheroes.domain.repository.CharacterRepository
import com.xavi.marvelheroes.domain.utils.Mapper
import com.xavi.marvelheroes.domain.utils.NetworkClient
import com.xavi.marvelheroes.domain.utils.State
import retrofit2.Retrofit

// region Repository
class CharacterRepositoryImp(
    dataSource: CharacterListDataSource,
//    cache: TimedCache,
    private val mapper: Mapper<CharactersDomainModel, MarvelResponseDTO<CharacterDTO>>
) : CharacterRepository,
    RetrofitRepository<CharacterListService, CharactersDomainModel, MarvelResponseDTO<CharacterDTO>>(
        dataSource
    ) {

    override suspend fun getCharacterList(): State<CharactersDomainModel> {
        return fetch(CharacterListPredicate(mapper))
    }
}
// endregion

// region DataSource
abstract class CharacterListDataSource(
    client: NetworkClient<Retrofit>
) : RetrofitDataSource<CharacterListService, CharactersDomainModel, MarvelResponseDTO<CharacterDTO>>(
    client
) {
    abstract suspend fun getInvestmentDashboard(predicate: CharacterListPredicate): State<CharactersDomainModel>
}

class CharacterListDataSourceImpl(
    client: NetworkClient<Retrofit>
) : CharacterListDataSource(client) {
    override suspend fun getInvestmentDashboard(predicate: CharacterListPredicate): State<CharactersDomainModel> {
        return fetch(predicate)
    }
}
// endregion

// region Predicate
class CharacterListPredicate(
    private val mapper: Mapper<CharactersDomainModel, MarvelResponseDTO<CharacterDTO>>
) : RetrofitPredicate<CharacterListService, CharactersDomainModel, MarvelResponseDTO<CharacterDTO>> {
    override fun mapper() = mapper
    override fun service() = CharacterListService::class.java
    override fun endpoint(): suspend (CharacterListService) -> MarvelResponseDTO<CharacterDTO> = {
        val auth = Auth()
        it.characters(ts = auth.ts, hash = auth.hash, apikey = auth.apikey)
    }
}
// endregion

// region Mapper

class CharactersMapper(
    private val characterMapper: Mapper<CharacterDomainModel, CharacterDTO>
) : Mapper<CharactersDomainModel, MarvelResponseDTO<CharacterDTO>> {

    companion object {
        const val NAME = "characters_mapper"
    }

    private val pageMapper: Mapper<PageDomainModel, PageDTO<CharacterDTO>> = PageMapper()

    override fun map(dto: MarvelResponseDTO<CharacterDTO>): CharactersDomainModel {
        val pageDto = dto.data ?: throw java.lang.IllegalArgumentException("page")
        val page = pageMapper.map(pageDto)
        val characters = pageDto.results?.map { characterMapper.map(it) } ?: listOf()
        return CharactersDomainModel(page, characters)
    }
}

class ThumbnailMapper : Mapper<ThumbnailDomainModel, ThumbnailDTO> {

    companion object {
        const val NAME = "thumbnail_mapper"
    }

    override fun map(dto: ThumbnailDTO): ThumbnailDomainModel {
        val path = dto.path ?: throw IllegalArgumentException("path")
        val extension = dto.extension ?: throw IllegalArgumentException("extension")
        return ThumbnailDomainModel(url = "$path.$extension")
    }
}

class CharacterMapper(
    private val thumbnailMapper: Mapper<ThumbnailDomainModel, ThumbnailDTO>
) : Mapper<CharacterDomainModel, CharacterDTO> {

    companion object {
        const val NAME = "character_mapper"
    }

    override fun map(dto: CharacterDTO): CharacterDomainModel {
        val id = dto.id ?: throw IllegalArgumentException("id")
        val thumbnail = mapThumbnail(dto.thumbnail)
        return CharacterDomainModel(id = id, name = dto.name, thumbnail = thumbnail)
    }

    @Suppress("SwallowedException")
    private fun mapThumbnail(dto: ThumbnailDTO?): ThumbnailDomainModel? {
        dto ?: return null
        return try {
            thumbnailMapper.map(dto)
        } catch (e: IllegalArgumentException) {
            null
        }
    }
}

// endregion
