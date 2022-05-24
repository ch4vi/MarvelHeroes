package com.xavi.marvelheroes.presentation.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

open class BaseViewModel : ViewModel() {

    operator fun invoke(
        dispatcher: CoroutineDispatcher,
        block: suspend CoroutineScope.() -> Unit
    ) = viewModelScope.launch(dispatcher) { block() }

    operator fun invoke(block: suspend CoroutineScope.() -> Unit) =
        viewModelScope.launch {
            block()
        }
}
