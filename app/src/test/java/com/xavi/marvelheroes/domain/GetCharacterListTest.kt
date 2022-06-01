package com.xavi.marvelheroes.domain

import androidx.paging.PagingData
import com.xavi.marvelheroes.domain.model.CharacterDomainModel
import com.xavi.marvelheroes.domain.model.Failure
import com.xavi.marvelheroes.domain.repository.CharacterListRepository
import com.xavi.marvelheroes.domain.usecase.GetCharacterList
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class GetCharacterListTest {

    @RelaxedMockK
    lateinit var repository: CharacterListRepository

    private lateinit var useCase: GetCharacterList

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        useCase = GetCharacterList(repository)
    }

    @Test
    fun `GIVEN GetCharacterList WHEN repository returns Success THEN returns data`() = runTest {
        val expected: Flow<PagingData<CharacterDomainModel>> =
            flow { emit(PagingData.from(emptyList())) }
        coEvery { repository.getCharacterList(any()) } returns expected

        val output = useCase(Unit)
        output.first()

        verify { repository.getCharacterList(any()) }
        assertEquals(1, output.count())
    }

    @Test(expected = Throwable::class)
    fun `GIVEN GetCharacterList WHEN repository returns Failure THEN throw exception`() = runTest {
        val exception = Failure.Unexpected()
        coEvery { repository.getCharacterList() } throws exception

        useCase(Unit).first()
    }
}
