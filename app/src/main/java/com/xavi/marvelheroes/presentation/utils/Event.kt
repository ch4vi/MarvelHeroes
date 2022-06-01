package com.xavi.marvelheroes.presentation.utils

import androidx.lifecycle.Observer

open class Event<out T>(private val content: T) {

    private var isUsed = false

    fun get(): T? {
        return if (isUsed) null
        else {
            isUsed = true
            content
        }
    }

    fun forceGet(): T = content
}

class EventObserver<T>(private val onEventUnhandledContent: (T) -> Unit) : Observer<Event<T>> {
    override fun onChanged(event: Event<T>?) {
        event?.get()?.let { value ->
            onEventUnhandledContent(value)
        }
    }
}

fun <T> T.toEvent() = Event(this)
