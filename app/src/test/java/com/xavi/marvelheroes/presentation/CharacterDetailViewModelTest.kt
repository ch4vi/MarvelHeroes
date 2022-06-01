package com.xavi.marvelheroes.presentation

import androidx.lifecycle.Observer
import com.xavi.marvelheroes.constants.DispatcherTest
import com.xavi.marvelheroes.constants.character
import com.xavi.marvelheroes.domain.model.CharacterDomainModel
import com.xavi.marvelheroes.domain.model.Failure
import com.xavi.marvelheroes.domain.usecase.GetCharacterDetail
import com.xavi.marvelheroes.presentation.utils.Event
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class CharacterDetailViewModelTest : DispatcherTest() {
    @RelaxedMockK
    private lateinit var useCase: GetCharacterDetail

    @RelaxedMockK
    private lateinit var observer: Observer<Event<CharacterDetailViewState>>

    private lateinit var viewModel: CharacterDetailViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel = CharacterDetailViewModel(useCase)
    }

    @Test
    fun `GIVEN ViewModel WHEN GetCharacter event THEN show data`() = runTest {
        viewModel.viewState.observeForever(observer)

        val expected: Flow<CharacterDomainModel> =
            flow { emit(character) }
        coEvery { useCase.invoke(any()) } returns expected

        viewModel.dispatch(CharacterDetailEvent.GetCharacter("foo"))
        val captor = mutableListOf<Event<CharacterDetailViewState>>()
        coVerify { observer.onChanged(capture(captor)) }

        assertTrue(captor[0].forceGet() is CharacterDetailViewState.ShowData)
    }

    @Test(expected = Throwable::class)
    fun `GIVEN ViewModel WHEN GetCharacter event THEN return exception`() = runTest {
        viewModel.viewState.observeForever(observer)

        val expected = Failure.Unexpected(reason = "foo")
        coEvery { useCase.invoke(any()) } throws expected

        viewModel.dispatch(CharacterDetailEvent.GetCharacter("foo"))
        val captor = mutableListOf<Event<CharacterDetailViewState>>()
        coVerify { observer.onChanged(capture(captor)) }
    }
}
