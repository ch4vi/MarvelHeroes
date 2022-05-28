package com.xavi.marvelheroes.data.datasource

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.room.withTransaction
import com.xavi.marvelheroes.data.api.CharacterListService
import com.xavi.marvelheroes.data.api.RetrofitMediator
import com.xavi.marvelheroes.data.datasource.CharacterListMediator.Flags.END_OF_PAGINATION
import com.xavi.marvelheroes.data.datasource.PageDefault.INITIAL_OFFSET
import com.xavi.marvelheroes.data.db.AppDatabase
import com.xavi.marvelheroes.data.db.CharacterDB
import com.xavi.marvelheroes.data.db.PageDB
import com.xavi.marvelheroes.data.model.CharacterDTO
import com.xavi.marvelheroes.data.model.MarvelResponseDTO
import com.xavi.marvelheroes.domain.model.CharacterDomainModel
import com.xavi.marvelheroes.domain.model.CharactersDomainModel
import com.xavi.marvelheroes.domain.utils.DBMapper
import com.xavi.marvelheroes.domain.utils.Mapper
import com.xavi.marvelheroes.domain.utils.NetworkClient
import com.xavi.marvelheroes.domain.utils.State
import java.io.IOException
import java.io.InvalidObjectException
import retrofit2.HttpException
import retrofit2.Retrofit

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

    private object Flags {
        const val END_OF_PAGINATION = -1
    }

    override val networkClient: NetworkClient<Retrofit>
        get() = client

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CharacterDB>
    ): MediatorResult {

        try {
            val offset = getPageData(loadType, state)
            if (offset == END_OF_PAGINATION)
                return MediatorResult.Success(endOfPaginationReached = true)

            val params = RequestParams(
                offset = offset,
                limit = state.config.pageSize,
            )

            val predicate = CharacterListPredicate(params, apiMapper)
            return when (val response = fetch(predicate)) {
                is State.Success -> updateCache(loadType, state, offset, response.data.characters)
                is State.Error -> MediatorResult.Error(response.error)
            }
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
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
            }
            val previousOffset = previous(offset, state.config.pageSize)
            val nextOffset = next(offset, state.config.pageSize)
            val pages = characters.map {
                PageDB(id = it.id, previous = previousOffset, next = nextOffset)
            }
            db.pageDao().insertAll(pages)
            db.characterDao().insertAll(characters.map { dbMapper.mapToDB(it) })
        }
        return MediatorResult.Success(endOfPaginationReached = isEOD)
    }

    private suspend fun getInitialPage(state: PagingState<Int, CharacterDB>): PageDB? {
        return state.pages
            .firstOrNull { it.data.isNotEmpty() }
            ?.data?.firstOrNull()
            ?.let { character ->
                db.pageDao().getById(character.id)
            }
    }

    private suspend fun getLastPage(state: PagingState<Int, CharacterDB>): PageDB? {
        return state.pages
            .lastOrNull { it.data.isNotEmpty() }
            ?.data?.lastOrNull()
            ?.let { character ->
                db.pageDao().getById(character.id)
            }
    }

    private suspend fun findClosestPage(state: PagingState<Int, CharacterDB>): PageDB? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { repoId ->
                db.pageDao().getById(repoId)
            }
        }
    }

    private suspend fun getPageData(
        loadType: LoadType,
        state: PagingState<Int, CharacterDB>
    ): Int {
        return when (loadType) {
            LoadType.REFRESH ->
                findClosestPage(state)?.let {
                    it.next?.minus(1)
                } ?: INITIAL_OFFSET
            LoadType.APPEND ->
                getLastPage(state)?.next
                    ?: INITIAL_OFFSET
            LoadType.PREPEND -> {
                val page = getInitialPage(state)
                    ?: throw InvalidObjectException("Invalid initial page")
                page.previous ?: END_OF_PAGINATION
            }
        }
    }
}
