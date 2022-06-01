package com.xavi.marvelheroes.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.xavi.marvelheroes.domain.model.CharacterDomainModel
import com.xavi.marvelheroes.domain.model.Failure
import com.xavi.marvelheroes.domain.usecase.GetCharacterDetail
import com.xavi.marvelheroes.presentation.utils.BaseViewModel
import com.xavi.marvelheroes.presentation.utils.Event
import com.xavi.marvelheroes.presentation.utils.toEvent
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged

sealed class CharacterDetailEvent {
    class GetCharacter(val id: String) : CharacterDetailEvent()
}

sealed class CharacterDetailViewState {
    class ShowData(val data: CharacterDomainModel) : CharacterDetailViewState()
    class OnFailure(val error: Throwable) : CharacterDetailViewState()
    object OnNotFound : CharacterDetailViewState()
}

class CharacterDetailViewModel(
    private val getCharacterDetail: GetCharacterDetail,
) : BaseViewModel() {

    private val mutableViewState: MutableLiveData<Event<CharacterDetailViewState>> =
        MutableLiveData()
    val viewState: LiveData<Event<CharacterDetailViewState>>
        get() = mutableViewState

    fun dispatch(event: CharacterDetailEvent) {
        when (event) {
            is CharacterDetailEvent.GetCharacter -> getCharacter(event.id)
        }
    }

    private fun getCharacter(id: String) {
        this {
            getCharacterDetail(GetCharacterDetail.Param(id))
                .catch { onFailure(it) }
                .distinctUntilChanged()
                .collectLatest {
                    changeState(CharacterDetailViewState.ShowData(it))
                }
        }
    }

    private fun onFailure(t: Throwable) {
        when (t) {
            is Failure.NotFound -> changeState(CharacterDetailViewState.OnNotFound)
            else -> changeState(CharacterDetailViewState.OnFailure(t))
        }
    }

    private fun changeState(state: CharacterDetailViewState) {
        mutableViewState.postValue(state.toEvent())
    }
}
