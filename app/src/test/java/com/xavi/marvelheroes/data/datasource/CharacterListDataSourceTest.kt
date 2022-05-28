package com.xavi.marvelheroes.data.datasource

import androidx.paging.PagingSource
import com.xavi.marvelheroes.constants.characterListDTO
import com.xavi.marvelheroes.constants.charactersList
import com.xavi.marvelheroes.constants.mockFetch
import com.xavi.marvelheroes.data.api.CharacterListService
import com.xavi.marvelheroes.data.mapper.CharacterMapper
import com.xavi.marvelheroes.data.mapper.CharactersMapper
import com.xavi.marvelheroes.data.mapper.ThumbnailMapper
import com.xavi.marvelheroes.data.model.CharacterDTO
import com.xavi.marvelheroes.data.model.MarvelResponseDTO
import com.xavi.marvelheroes.domain.model.CharacterDomainModel
import com.xavi.marvelheroes.domain.model.CharactersDomainModel
import com.xavi.marvelheroes.domain.model.Failure
import com.xavi.marvelheroes.domain.utils.Mapper
import com.xavi.marvelheroes.domain.utils.NetworkClient
import com.xavi.marvelheroes.domain.utils.State
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.koin.test.AutoCloseKoinTest
import retrofit2.Retrofit

class CharacterListDataSourceTest : AutoCloseKoinTest() {

    private lateinit var sut: CharacterListDataSource
    private lateinit var apiMapper: Mapper<CharactersDomainModel, MarvelResponseDTO<CharacterDTO>>

    private val networkClient = mockk<NetworkClient<Retrofit>>()
    private val service = mockk<CharacterListService>()

    @Before
    fun setUp() {
        mockFetch(networkClient, CharacterListService::class.java, service)
        apiMapper = CharactersMapper(CharacterMapper(ThumbnailMapper()))
        sut = CharacterListDataSource(networkClient, apiMapper, "foo")
    }

    @Test
    fun `GIVEN PagedDataSource WHEN load THEN return data`() = runBlocking {
        val expected = characterListDTO
        coEvery { service.characters(any(), any(), any(), any(), any(), any()) } returns expected

        val data = charactersList.characters
        val expectedResult = PagingSource.LoadResult.Page(
            data = data,
            prevKey = null,
            nextKey = 2
        )
        assertEquals(
            expectedResult,
            sut.load(
                PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = 2,
                    placeholdersEnabled = false
                )
            )
        )
    }

    @Test(expected = Throwable::class)
    fun `GIVEN PagedDataSource WHEN load THEN return exception`() = runBlocking {
        val error = RuntimeException("404", Throwable())
        coEvery { service.characters(any(), any(), any(), any(), any(), any()) } throws error

        val expectedResult =
            PagingSource.LoadResult.Error<Int, CharacterDomainModel>(
                Failure.Unexpected(error.message)
            )
        assertEquals(
            expectedResult,
            sut.load(
                PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = 1,
                    placeholdersEnabled = false
                )
            )
        )
    }

    @Test
    fun `GIVEN getCharacterList WHEN fetch THEN model is filled`() = runBlocking {
        val expected = characterListDTO
        coEvery { service.characters(any(), any(), any(), any(), any(), any()) } returns expected

        val requestParams = RequestParams(0, 0)
        val response =
            sut.fetch(CharacterListPredicate(requestParams, apiMapper))

        assertTrue(response.isSuccess)
        response as State.Success
        assertNotNull(response.data)
        assertNotNull(response.data.characters.first())
    }

    @Test
    fun `GIVEN getCharacterList WHEN fetch AND errorResponse THEN error contains throwable`() =
        runBlocking {
            val expected = Failure.Unexpected()
            coEvery { service.characters(any(), any(), any(), any(), any(), any()) } throws expected

            val requestParams = RequestParams(0, 0)
            val response =
                sut.fetch(CharacterListPredicate(requestParams, apiMapper))

            assertTrue(response.isFailure)
            response as State.Error
            assertNotNull(response.error)
        }
}
