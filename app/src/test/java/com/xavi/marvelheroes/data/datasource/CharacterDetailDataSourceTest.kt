package com.xavi.marvelheroes.data.datasource

import com.xavi.marvelheroes.constants.characterListDTO
import com.xavi.marvelheroes.constants.mockFetch
import com.xavi.marvelheroes.data.api.CharacterDetailService
import com.xavi.marvelheroes.data.mapper.CharacterMapper
import com.xavi.marvelheroes.data.mapper.CharactersMapper
import com.xavi.marvelheroes.data.mapper.ThumbnailMapper
import com.xavi.marvelheroes.data.model.CharacterDTO
import com.xavi.marvelheroes.data.model.MarvelResponseDTO
import com.xavi.marvelheroes.domain.model.CharactersDomainModel
import com.xavi.marvelheroes.domain.model.Failure
import com.xavi.marvelheroes.domain.utils.Mapper
import com.xavi.marvelheroes.domain.utils.NetworkClient
import com.xavi.marvelheroes.domain.utils.State
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.koin.test.AutoCloseKoinTest
import retrofit2.Retrofit

@ExperimentalCoroutinesApi
class CharacterDetailDataSourceTest : AutoCloseKoinTest() {

    @RelaxedMockK
    lateinit var networkClient: NetworkClient<Retrofit>

    @RelaxedMockK
    lateinit var service: CharacterDetailService

    private lateinit var sut: CharacterDetailDataSource
    private lateinit var mapper: Mapper<CharactersDomainModel, MarvelResponseDTO<CharacterDTO>>

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mockFetch(networkClient, CharacterDetailService::class.java, service)
        mapper = CharactersMapper(CharacterMapper(ThumbnailMapper()))
        sut = CharacterDetailDataSource(networkClient)
    }

    @Test
    fun `GIVEN DataSource WHEN load THEN return data`() = runTest {
        val expected = characterListDTO
        coEvery { service.characterDetail(any(), any(), any(), any()) } returns expected

        val output = sut.load(CharacterDetailPredicate("foo", mapper))
        assertTrue(output is State.Success)
        output as State.Success
        assertTrue(output.data.characters.isNotEmpty())
    }

    @Test
    fun `GIVEN DataSource WHEN load THEN return exception`() = runTest {
        val error = RuntimeException("404", Throwable())
        coEvery { service.characterDetail(any(), any(), any(), any()) } throws error

        val output = sut.load(CharacterDetailPredicate("foo", mapper))
        assert(output is State.Error)
    }

    @Test
    fun `GIVEN characterDetail WHEN fetch THEN model is filled`() = runTest {
        val expected = characterListDTO
        coEvery { service.characterDetail(any(), any(), any(), any()) } returns expected

        val response = sut.fetch(CharacterDetailPredicate("foo", mapper))

        assertTrue(response.isSuccess)
        response as State.Success
        assertNotNull(response.data)
        assertNotNull(response.data.characters.first())
    }

    @Test
    fun `GIVEN characterDetail WHEN fetch AND errorResponse THEN error contains throwable`() =
        runTest {
            val expected = Failure.Unexpected()
            coEvery { service.characterDetail(any(), any(), any(), any()) } throws expected

            val response = sut.fetch(CharacterDetailPredicate("foo", mapper))

            assertTrue(response.isFailure)
            response as State.Error
            assertNotNull(response.error)
        }
}
