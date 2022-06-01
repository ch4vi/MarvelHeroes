package com.xavi.marvelheroes.data.datasource

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingState
import androidx.paging.map
import com.xavi.marvelheroes.data.api.CharacterListService
import com.xavi.marvelheroes.data.api.RetrofitPagedPredicate
import com.xavi.marvelheroes.data.api.RetrofitPagedSource
import com.xavi.marvelheroes.data.api.RetrofitRepository
import com.xavi.marvelheroes.data.datasource.PageDefault.DEFAULT_LIMIT
import com.xavi.marvelheroes.data.datasource.PageDefault.INITIAL_OFFSET
import com.xavi.marvelheroes.data.db.AppDatabase
import com.xavi.marvelheroes.data.db.CharacterDB
import com.xavi.marvelheroes.data.model.CharacterDTO
import com.xavi.marvelheroes.data.model.MarvelResponseDTO
import com.xavi.marvelheroes.domain.model.CharacterDomainModel
import com.xavi.marvelheroes.domain.model.CharactersDomainModel
import com.xavi.marvelheroes.domain.repository.CharacterListRepository
import com.xavi.marvelheroes.domain.utils.DBMapper
import com.xavi.marvelheroes.domain.utils.Mapper
import com.xavi.marvelheroes.domain.utils.NetworkClient
import com.xavi.marvelheroes.domain.utils.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import retrofit2.Retrofit

// region Repository

@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(ExperimentalPagingApi::class)
class CharacterListRepositoryImp(
    private val client: NetworkClient<Retrofit>,
    private val db: AppDatabase,
    private val apiMapper: Mapper<CharactersDomainModel, MarvelResponseDTO<CharacterDTO>>,
    private val dbMapper: DBMapper<CharacterDomainModel, CharacterDB>,
) : CharacterListRepository,
    RetrofitRepository<CharacterListService, CharactersDomainModel, MarvelResponseDTO<CharacterDTO>> {

    override fun getCharacterList(pagingConfig: PagingConfig): Flow<PagingData<CharacterDomainModel>> {
        val pagingSourceFactory = { db.characterDao().getAll() }
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = pagingSourceFactory,
            remoteMediator = CharacterListMediator(client, db, apiMapper, dbMapper)
        ).flow.transform {
            emit(
                it.map { dbItem ->
                    dbMapper.mapToDomain(dbItem)
                }
            )
        }
    }

    override fun searchCharacterList(
        pagingConfig: PagingConfig,
        queryName: String?
    ): Flow<PagingData<CharacterDomainModel>> {
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { CharacterListDataSource(client, apiMapper, queryName) }
        ).flow
    }
}

// endregion

// region DataSource

class CharacterListDataSource(
    client: NetworkClient<Retrofit>,
    private val mapper: Mapper<CharactersDomainModel, MarvelResponseDTO<CharacterDTO>>,
    private val queryName: String?,
) : RetrofitPagedSource<
    CharacterListService,
    CharacterDomainModel,
    CharactersDomainModel,
    MarvelResponseDTO<CharacterDTO>>() {

    override val networkClient = client

    override fun getRefreshKey(state: PagingState<Int, CharacterDomainModel>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CharacterDomainModel> {
        val current = params.key ?: INITIAL_OFFSET
        val requestParams =
            RequestParams(offset = current, limit = params.loadSize, queryName = queryName)
        val predicate = CharacterListPredicate(requestParams, mapper)

        return when (val result = fetch(predicate)) {
            is State.Success -> {
                val isEOF = result.data.characters.isEmpty()
                val prev = previous(current, params.loadSize)
                val next = if (isEOF) null else next(current, params.loadSize)
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
    private val params: RequestParams,
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
            limit = params.limit,
            offset = params.offset,
            queryName = params.queryName
        )
    }
}

object PageDefault {
    const val DEFAULT_LIMIT = 20
    const val INITIAL_OFFSET = 0
}

data class RequestParams(
    val limit: Int = DEFAULT_LIMIT,
    val offset: Int = 0,
    val queryName: String? = null
)

fun previous(current: Int, limit: Int = DEFAULT_LIMIT) =
    if (current == INITIAL_OFFSET) null
    else Integer.max(current - limit, INITIAL_OFFSET)

fun next(current: Int, limit: Int = DEFAULT_LIMIT) = current + limit

// endregion
