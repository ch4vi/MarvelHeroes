package com.xavi.marvelheroes.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.xavi.marvelheroes.domain.model.CharacterDomainModel
import com.xavi.marvelheroes.domain.usecase.GetCharacterList
import com.xavi.marvelheroes.presentation.utils.BaseViewModel
import com.xavi.marvelheroes.presentation.utils.Event
import com.xavi.marvelheroes.presentation.utils.toEvent
import kotlinx.coroutines.flow.catch

sealed class CharacterListEvent {
    object GetCharacters : CharacterListEvent()
}

sealed class CharacterListViewState {
    class OnFailure(val error: Throwable) : CharacterListViewState()
    class ShowData(val data: PagingData<CharacterDomainModel>) : CharacterListViewState()
}

class CharacterListViewModel(
    private val getCharacterList: GetCharacterList,
) : BaseViewModel() {

    private val mutableViewState: MutableLiveData<Event<CharacterListViewState>> = MutableLiveData()
    val viewState: LiveData<Event<CharacterListViewState>>
        get() = mutableViewState

    fun dispatch(event: CharacterListEvent) {
        when (event) {
            is CharacterListEvent.GetCharacters -> getAll()
        }
    }

    private fun getAll() {
        this {
            getCharacterList(Unit)
                .cachedIn(viewModelScope)
                .catch { onFailure(it) }
                .collect {
                    changeState(CharacterListViewState.ShowData(it))
                }
        }
    }

    private fun onFailure(t: Throwable) {
        changeState(CharacterListViewState.OnFailure(t))
    }

    private fun changeState(state: CharacterListViewState) {
        mutableViewState.postValue(state.toEvent())
    }
}
