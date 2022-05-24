package com.xavi.marvelheroes.domain

import com.xavi.marvelheroes.constants.charactersList
import com.xavi.marvelheroes.domain.model.Failure
import com.xavi.marvelheroes.domain.repository.CharacterRepository
import com.xavi.marvelheroes.domain.usecase.GetCharacterList
import com.xavi.marvelheroes.domain.utils.State
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetCharacterListTest {
    private lateinit var useCase: GetCharacterList

    @RelaxedMockK
    lateinit var repository: CharacterRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        useCase = GetCharacterList(repository)
    }

    @Test
    fun `GIVEN GetCharacterList WHEN dataSource returns Success THEN emit callback`() {
        val expected = charactersList

        coEvery { repository.getCharacterList() } returns State.Success(expected)

        runBlocking {
            val output = useCase(Unit).first()
            assertEquals(expected.page.count, output.page.count)
            assertEquals(expected.characters.size, output.characters.size)
        }
    }

    @Test(expected = Throwable::class)
    fun `GIVEN GetCharacterList WHEN dataSource returns Failure THEN throw exception`() {
        val exception = Failure.Unexpected()

        coEvery { repository.getCharacterList() } throws exception

        runBlocking {
            useCase(Unit).first()
        }
    }
}
