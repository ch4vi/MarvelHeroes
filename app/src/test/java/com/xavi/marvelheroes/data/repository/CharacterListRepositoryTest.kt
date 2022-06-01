package com.xavi.marvelheroes.data.repository

import com.xavi.marvelheroes.constants.PagingSourceTest
import com.xavi.marvelheroes.constants.characterDB
import com.xavi.marvelheroes.constants.otherCharacterDB
import com.xavi.marvelheroes.data.datasource.CharacterListRepositoryImp
import com.xavi.marvelheroes.data.db.AppDatabase
import com.xavi.marvelheroes.data.db.CharacterDB
import com.xavi.marvelheroes.data.mapper.CharacterDBMapper
import com.xavi.marvelheroes.data.mapper.CharacterMapper
import com.xavi.marvelheroes.data.mapper.CharactersMapper
import com.xavi.marvelheroes.data.mapper.ThumbnailMapper
import com.xavi.marvelheroes.data.model.CharacterDTO
import com.xavi.marvelheroes.data.model.MarvelResponseDTO
import com.xavi.marvelheroes.domain.model.CharacterDomainModel
import com.xavi.marvelheroes.domain.model.CharactersDomainModel
import com.xavi.marvelheroes.domain.model.Failure
import com.xavi.marvelheroes.domain.repository.CharacterListRepository
import com.xavi.marvelheroes.domain.utils.DBMapper
import com.xavi.marvelheroes.domain.utils.Mapper
import com.xavi.marvelheroes.domain.utils.NetworkClient
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.koin.test.AutoCloseKoinTest
import retrofit2.Retrofit

@ExperimentalCoroutinesApi
class CharacterListRepositoryTest : AutoCloseKoinTest() {

    @RelaxedMockK
    lateinit var networkClient: NetworkClient<Retrofit>

    @RelaxedMockK
    lateinit var databaseClient: AppDatabase

    private lateinit var apiMapper: Mapper<CharactersDomainModel, MarvelResponseDTO<CharacterDTO>>
    private lateinit var dbMapper: DBMapper<CharacterDomainModel, CharacterDB>
    private lateinit var repository: CharacterListRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        apiMapper = CharactersMapper(CharacterMapper(ThumbnailMapper()))
        dbMapper = CharacterDBMapper()
        repository = CharacterListRepositoryImp(networkClient, databaseClient, apiMapper, dbMapper)
    }

    @Test
    fun `GIVEN Repository WHEN fetch THEN model is filled`() = runTest {
        val expected = PagingSourceTest(data = listOf(characterDB, otherCharacterDB))
        coEvery { databaseClient.characterDao().getAll() } returns expected

        val response = repository.getCharacterList().first()
        assertNotNull(response)
    }

    @Test
    fun `GIVEN Repository WHEN search THEN model is filled`() = runTest {
        val response = repository.searchCharacterList(queryName = "foo").first()
        assertNotNull(response)
    }

    @Test
    fun `GIVEN Repository WHEN error THEN error is thrown`() = runTest {
        val expected = Failure.Unexpected()
        coEvery { networkClient.client() } throws expected

        repository.getCharacterList()
    }
}
