package com.xavi.marvelheroes.data.datasource

import com.xavi.marvelheroes.constants.characterListDTO
import com.xavi.marvelheroes.constants.mockFetch
import com.xavi.marvelheroes.data.api.CharacterListService
import com.xavi.marvelheroes.data.model.CharacterDTO
import com.xavi.marvelheroes.data.model.MarvelResponseDTO
import com.xavi.marvelheroes.domain.model.CharactersDomainModel
import com.xavi.marvelheroes.domain.model.Failure
import com.xavi.marvelheroes.domain.utils.Mapper
import com.xavi.marvelheroes.domain.utils.NetworkClient
import com.xavi.marvelheroes.domain.utils.State
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.koin.test.AutoCloseKoinTest
import retrofit2.Retrofit

class CharacterListDataSourceTest : AutoCloseKoinTest() {

    private lateinit var sut: CharacterListDataSource
    private lateinit var mapper: Mapper<CharactersDomainModel, MarvelResponseDTO<CharacterDTO>>

    private val networkClient = mockk<NetworkClient<Retrofit>>()
    private val service = mockk<CharacterListService>()

    @Before
    fun setUp() {
        mockFetch(networkClient, CharacterListService::class.java, service)
        sut = CharacterListDataSourceImpl(networkClient)
        mapper = CharactersMapper(CharacterMapper(ThumbnailMapper()))
    }

    @Test
    fun `GIVEN getCharacterList WHEN fetch THEN model is filled`() {
        runBlocking {
            val expected = characterListDTO
            coEvery { service.characters(any(), any(), any()) } returns expected

            val response =
                sut.getCharacterList(CharacterListPredicate(mapper))

            assertTrue(response.isSuccess)
            response as State.Success
            assertNotNull(response.data)
            assertNotNull(response.data.characters.first())
        }
    }

    @Test
    fun `GIVEN getCharacterList WHEN fetch AND errorResponse THEN error contains throwable`() {
        runBlocking {
            val expected = Failure.Unexpected()
            coEvery { service.characters(any(), any(), any()) } throws expected

            val response =
                sut.getCharacterList(CharacterListPredicate(mapper))

            assertTrue(response.isFailure)
            response as State.Error
            assertNotNull(response.error)
        }
    }
}
