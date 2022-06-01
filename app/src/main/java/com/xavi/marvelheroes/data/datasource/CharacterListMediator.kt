package com.xavi.marvelheroes.data.datasource

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.room.withTransaction
import com.xavi.marvelheroes.data.api.CharacterListService
import com.xavi.marvelheroes.data.api.RetrofitMediator
import com.xavi.marvelheroes.data.datasource.PageDefault.INITIAL_OFFSET
import com.xavi.marvelheroes.data.db.AppDatabase
import com.xavi.marvelheroes.data.db.CharacterDB
import com.xavi.marvelheroes.data.db.LastUpdateDB
import com.xavi.marvelheroes.data.db.PageDB
import com.xavi.marvelheroes.data.model.CharacterDTO
import com.xavi.marvelheroes.data.model.MarvelResponseDTO
import com.xavi.marvelheroes.domain.model.CharacterDomainModel
import com.xavi.marvelheroes.domain.model.CharactersDomainModel
import com.xavi.marvelheroes.domain.utils.DBMapper
import com.xavi.marvelheroes.domain.utils.Mapper
import com.xavi.marvelheroes.domain.utils.NetworkClient
import com.xavi.marvelheroes.domain.utils.State
import retrofit2.HttpException
import retrofit2.Retrofit
import java.io.IOException
import java.util.concurrent.TimeUnit

@ExperimentalPagingApi
class CharacterListMediator(
    private val client: NetworkClient<Retrofit>,
    private val db: AppDatabase,
    private val apiMapper: Mapper<CharactersDomainModel, MarvelResponseDTO<CharacterDTO>>,
    private val dbMapper: DBMapper<CharacterDomainModel, CharacterDB>,
) : RetrofitMediator<
    CharacterListService,
    CharactersDomainModel,
    MarvelResponseDTO<CharacterDTO>,
    CharacterDB>() {

    override val networkClient: NetworkClient<Retrofit>
        get() = client

    override suspend fun initialize(): InitializeAction {
        val initialAction = db.lastUpdate().get()?.timestamp?.let { lastUpdated ->
            val cacheTimeout = TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS)
            if (System.currentTimeMillis() - lastUpdated >= cacheTimeout) {
                InitializeAction.LAUNCH_INITIAL_REFRESH
            } else {
                InitializeAction.SKIP_INITIAL_REFRESH
            }
        }
        return initialAction ?: InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CharacterDB>
    ): MediatorResult {
        try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKey = db.withTransaction {
                        getLastPage(state)
                    }

                    if (remoteKey?.next == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }

                    remoteKey.next
                }
            }

            val offset = loadKey ?: INITIAL_OFFSET
            val params = RequestParams(
                offset = offset,
                limit = when (loadType) {
                    LoadType.REFRESH -> state.config.initialLoadSize
                    else -> state.config.pageSize
                }
            )

            val predicate = CharacterListPredicate(params, apiMapper)
            return when (val response = fetch(predicate)) {
                is State.Success -> updateCache(loadType, state, offset, response.data.characters)
                is State.Error -> MediatorResult.Error(response.error)
            }
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun updateCache(
        loadType: LoadType,
        state: PagingState<Int, CharacterDB>,
        offset: Int,
        characters: List<CharacterDomainModel>,
    ): MediatorResult {
        val isEOD = characters.isEmpty()
        db.withTransaction {
            if (loadType == LoadType.REFRESH) {
                db.pageDao().clearAll()
                db.characterDao().clearAll()
                db.lastUpdate().clearAll()
            }
            val previousOffset = previous(offset, state.config.pageSize)
            val nextOffset = next(offset, state.config.pageSize)
            val pages = characters.map {
                PageDB(id = it.id, previous = previousOffset, next = nextOffset)
            }
            db.pageDao().insertAll(pages)
            db.characterDao().insertAll(characters.map { dbMapper.mapToDB(it) })
            db.lastUpdate().insert(LastUpdateDB(timestamp = System.currentTimeMillis()))
        }
        return MediatorResult.Success(endOfPaginationReached = isEOD)
    }

    private suspend fun getLastPage(state: PagingState<Int, CharacterDB>): PageDB? {
        return state.pages
            .lastOrNull { it.data.isNotEmpty() }
            ?.data?.lastOrNull()
            ?.let { character ->
                db.pageDao().getById(character.id)
            }
    }
}
