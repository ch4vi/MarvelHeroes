package com.xavi.marvelheroes.data.datasource

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingState
import com.xavi.marvelheroes.data.api.CharacterListService
import com.xavi.marvelheroes.data.api.RetrofitPagedPredicate
import com.xavi.marvelheroes.data.api.RetrofitPagedSource
import com.xavi.marvelheroes.data.api.RetrofitRepository
import com.xavi.marvelheroes.data.model.CharacterDTO
import com.xavi.marvelheroes.data.model.MarvelResponseDTO
import com.xavi.marvelheroes.domain.model.CharacterDomainModel
import com.xavi.marvelheroes.domain.model.CharactersDomainModel
import com.xavi.marvelheroes.domain.model.PageDomainModel
import com.xavi.marvelheroes.domain.repository.CharacterRepository
import com.xavi.marvelheroes.domain.utils.Mapper
import com.xavi.marvelheroes.domain.utils.NetworkClient
import com.xavi.marvelheroes.domain.utils.State
import kotlinx.coroutines.flow.Flow
import retrofit2.Retrofit

// region Repository

class CharacterRepositoryImp(
    private val client: NetworkClient<Retrofit>,
    private val mapper: Mapper<CharactersDomainModel, MarvelResponseDTO<CharacterDTO>>,
) : CharacterRepository,
    RetrofitRepository<CharacterListService, CharactersDomainModel, MarvelResponseDTO<CharacterDTO>> {

    override fun getCharacterList(pagingConfig: PagingConfig): Flow<PagingData<CharacterDomainModel>> {
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { CharacterListDataSource(client, mapper) }
        ).flow
    }
}

// endregion

// region DataSource

class CharacterListDataSource(
    client: NetworkClient<Retrofit>,
    private val mapper: Mapper<CharactersDomainModel, MarvelResponseDTO<CharacterDTO>>,
) : RetrofitPagedSource<
    CharacterListService,
    CharacterDomainModel,
    CharactersDomainModel,
    MarvelResponseDTO<CharacterDTO>>() {

    override val networkClient = client

    override fun getRefreshKey(state: PagingState<PageDomainModel, CharacterDomainModel>): PageDomainModel? {
        return state.anchorPosition?.let {
            PageDomainModel(offset = it, limit = state.config.pageSize, 0, 0)
        }
    }

    override suspend fun load(params: LoadParams<PageDomainModel>): LoadResult<PageDomainModel, CharacterDomainModel> {
        var current = params.key ?: PageDomainModel.start()
        val predicate = CharacterListPredicate(current, mapper)

        return when (val result = fetch(predicate)) {
            is State.Success -> {
                if (current.total == 0) current = result.data.page
                val prev = predicate.previous(current)
                val next = predicate.next(current)
                LoadResult.Page(result.data.characters, prevKey = prev, nextKey = next)
            }
            is State.Error ->
                LoadResult.Error(result.error)
        }
    }
}

// endregion

// region Predicate

class CharacterListPredicate(
    private val page: PageDomainModel,
    private val mapper: Mapper<CharactersDomainModel, MarvelResponseDTO<CharacterDTO>>
) : RetrofitPagedPredicate<CharacterListService, CharactersDomainModel, MarvelResponseDTO<CharacterDTO>> {

    override fun mapper() = mapper
    override fun service() = CharacterListService::class.java
    override fun endpoint(): suspend (CharacterListService) -> MarvelResponseDTO<CharacterDTO> = {
        val auth = Auth()
        it.characters(
            ts = auth.ts,
            hash = auth.hash,
            apikey = auth.apikey,
            limit = page.limit,
            offset = page.offset
        )
    }
}

// endregion
