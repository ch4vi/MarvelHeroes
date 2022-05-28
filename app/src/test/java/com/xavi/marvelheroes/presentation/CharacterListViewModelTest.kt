package com.xavi.marvelheroes.presentation

import androidx.lifecycle.Observer
import androidx.paging.PagingData
import com.xavi.marvelheroes.constants.DispatcherTest
import com.xavi.marvelheroes.domain.model.CharacterDomainModel
import com.xavi.marvelheroes.domain.model.Failure
import com.xavi.marvelheroes.domain.usecase.GetCharacterList
import com.xavi.marvelheroes.domain.usecase.SearchCharacterList
import com.xavi.marvelheroes.presentation.utils.Event
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class CharacterListViewModelTest : DispatcherTest() {
    @RelaxedMockK
    lateinit var characterListUseCase: GetCharacterList

    @RelaxedMockK
    lateinit var searchCharacterUseCase: SearchCharacterList

    @RelaxedMockK
    private lateinit var observer: Observer<Event<CharacterListViewState>>

    private lateinit var viewModel: CharacterListViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel = CharacterListViewModel(characterListUseCase, searchCharacterUseCase)
    }

    /*
    sealed class CharacterListEvent {
    object GetCharacters : CharacterListEvent()
    class SearchCharacters(val name: String) : CharacterListEvent()
}

sealed class CharacterListViewState {
    class OnFailure(val error: Throwable) : CharacterListViewState()
    class ShowData(val data: PagingData<CharacterDomainModel>) : CharacterListViewState()
    class ShowQueryData(val data: PagingData<CharacterDomainModel>) : CharacterListViewState()
}
     */

    @Test
    fun `GIVEN ViewModel WHEN GetCharacters event THEN show data`() = runBlocking {
        viewModel.viewState.observeForever(observer)

        val expected: Flow<PagingData<CharacterDomainModel>> =
            flow { emit(PagingData.from(emptyList())) }
        coEvery { characterListUseCase.invoke(Unit) } returns expected

        viewModel.dispatch(CharacterListEvent.GetCharacters)
        val captor = mutableListOf<Event<CharacterListViewState>>()
        coVerify { observer.onChanged(capture(captor)) }

        assertTrue(captor[0].forceGet() is CharacterListViewState.ShowData)
    }

    @Test(expected = Throwable::class)
    fun `GIVEN ViewModel WHEN GetCharacters event THEN return exception`() = runBlocking {
        viewModel.viewState.observeForever(observer)

        val expected = Failure.Unexpected(reason = "foo")
        coEvery { characterListUseCase.invoke(Unit) } throws expected

        viewModel.dispatch(CharacterListEvent.GetCharacters)
        val captor = mutableListOf<Event<CharacterListViewState>>()
        coVerify { observer.onChanged(capture(captor)) }
    }

    @Test
    fun `GIVEN ViewModel WHEN SearchCharacterList event THEN show data`() = runBlocking {
        viewModel.viewState.observeForever(observer)

        val expected: Flow<PagingData<CharacterDomainModel>> =
            flow { emit(PagingData.from(emptyList())) }
        coEvery { searchCharacterUseCase.invoke(any()) } returns expected

        viewModel.dispatch(CharacterListEvent.SearchCharacters("foo"))
        val captor = mutableListOf<Event<CharacterListViewState>>()
        coVerify { observer.onChanged(capture(captor)) }

        assertTrue(captor[0].forceGet() is CharacterListViewState.ShowQueryData)
    }

    @Test(expected = Throwable::class)
    fun `GIVEN ViewModel WHEN SearchCharacterList event THEN return exception`() = runBlocking {
        viewModel.viewState.observeForever(observer)

        val expected = Failure.Unexpected(reason = "foo")
        coEvery { searchCharacterUseCase.invoke(any()) } throws expected

        viewModel.dispatch(CharacterListEvent.SearchCharacters("foo"))
        val captor = mutableListOf<Event<CharacterListViewState>>()
        coVerify { observer.onChanged(capture(captor)) }
    }
}
