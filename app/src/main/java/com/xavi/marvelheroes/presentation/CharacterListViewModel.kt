package com.xavi.marvelheroes.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.xavi.marvelheroes.domain.model.CharacterDomainModel
import com.xavi.marvelheroes.domain.usecase.GetCharacterList
import com.xavi.marvelheroes.domain.usecase.SearchCharacterList
import com.xavi.marvelheroes.presentation.utils.BaseViewModel
import com.xavi.marvelheroes.presentation.utils.Event
import com.xavi.marvelheroes.presentation.utils.toEvent

sealed class CharacterListEvent {
    object GetCharacters : CharacterListEvent()
    class SearchCharacters(val name: String) : CharacterListEvent()
}

sealed class CharacterListViewState {
    class ShowData(val data: PagingData<CharacterDomainModel>) : CharacterListViewState()
    class ShowQueryData(val data: PagingData<CharacterDomainModel>) : CharacterListViewState()
}

class CharacterListViewModel(
    private val getCharacterList: GetCharacterList,
    private val searchCharacterList: SearchCharacterList,
) : BaseViewModel() {

    private val mutableViewState: MutableLiveData<Event<CharacterListViewState>> = MutableLiveData()
    val viewState: LiveData<Event<CharacterListViewState>>
        get() = mutableViewState

    fun dispatch(event: CharacterListEvent) {
        when (event) {
            is CharacterListEvent.GetCharacters -> getAll()
            is CharacterListEvent.SearchCharacters -> searchByName(event.name)
        }
    }

    private fun getAll() {
        this {
            getCharacterList(Unit)
                .cachedIn(viewModelScope)
                .collect {
                    changeState(CharacterListViewState.ShowData(it))
                }
        }
    }

    private fun searchByName(name: String) {
        this {
            val query = name.ifBlank { null }
            searchCharacterList(SearchCharacterList.Param(queryName = query))
                .cachedIn(viewModelScope)
                .collect {
                    changeState(CharacterListViewState.ShowQueryData(it))
                }
        }
    }

    private fun changeState(state: CharacterListViewState) {
        mutableViewState.postValue(state.toEvent())
    }
}
