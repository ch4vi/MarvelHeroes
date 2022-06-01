package com.xavi.marvelheroes.data.repository

import com.xavi.marvelheroes.constants.charactersList
import com.xavi.marvelheroes.data.datasource.CharacterDetailDataSource
import com.xavi.marvelheroes.data.datasource.CharacterDetailRepositoryImp
import com.xavi.marvelheroes.data.mapper.CharacterMapper
import com.xavi.marvelheroes.data.mapper.CharactersMapper
import com.xavi.marvelheroes.data.mapper.ThumbnailMapper
import com.xavi.marvelheroes.data.model.CharacterDTO
import com.xavi.marvelheroes.data.model.MarvelResponseDTO
import com.xavi.marvelheroes.domain.model.CharactersDomainModel
import com.xavi.marvelheroes.domain.model.Failure
import com.xavi.marvelheroes.domain.repository.CharacterDetailRepository
import com.xavi.marvelheroes.domain.utils.Mapper
import com.xavi.marvelheroes.domain.utils.State
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

@ExperimentalCoroutinesApi
class CharacterDetailRepositoryTest : AutoCloseKoinTest() {

    @RelaxedMockK
    lateinit var dataSource: CharacterDetailDataSource

    private lateinit var mapper: Mapper<CharactersDomainModel, MarvelResponseDTO<CharacterDTO>>
    private lateinit var repository: CharacterDetailRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mapper = CharactersMapper(CharacterMapper(ThumbnailMapper()))
        repository = CharacterDetailRepositoryImp(dataSource, mapper)
    }

    @Test
    fun `GIVEN Repository WHEN fetch THEN model is filled`() = runTest {
        val expected = State.Success(charactersList)
        coEvery { dataSource.load(any()) } returns expected

        val response = repository.getCharacter("foo").first()
        assertNotNull(response)
    }

    @Test
    fun `GIVEN Repository WHEN error THEN error is thrown`() = runTest {
        val expected = Failure.Unexpected()

        coEvery { dataSource.load(any()) } throws expected
        repository.getCharacter("foo")
    }
}
