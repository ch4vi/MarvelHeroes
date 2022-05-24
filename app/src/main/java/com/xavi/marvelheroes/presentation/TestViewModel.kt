package com.xavi.marvelheroes.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.xavi.marvelheroes.domain.model.CharactersDomainModel
import com.xavi.marvelheroes.domain.usecase.GetCharacterList
import com.xavi.marvelheroes.presentation.utils.BaseViewModel
import com.xavi.marvelheroes.presentation.utils.Event
import com.xavi.marvelheroes.presentation.utils.toEvent
import kotlinx.coroutines.flow.catch

sealed class TestEvent {
    object GetCharacters : TestEvent()
}

sealed class TestViewState {
    object Loading : TestViewState()
    class OnFailure(val error: Throwable) : TestViewState()
    class ShowData(val data: CharactersDomainModel) : TestViewState()
}

class TestViewModel(
    private val getCharacterList: GetCharacterList
) : BaseViewModel() {

    private val mutableViewState: MutableLiveData<Event<TestViewState>> = MutableLiveData()
    val viewState: LiveData<Event<TestViewState>>
        get() = mutableViewState

    fun dispatch(event: TestEvent) {
        when (event) {
            is TestEvent.GetCharacters -> getAll()
        }
    }

    private fun getAll() {
        this {
            changeState(TestViewState.Loading)
            getCharacterList(Unit)
                .catch { onFailure(it) }
                .collect {
                    changeState(TestViewState.ShowData(it))
                }
        }
    }

    // TODO change
    private fun onFailure(t: Throwable) {
        changeState(TestViewState.OnFailure(t))
    }

    private fun changeState(state: TestViewState) {
        mutableViewState.postValue(state.toEvent())
    }
}
