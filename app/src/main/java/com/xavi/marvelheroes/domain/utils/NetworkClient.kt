package com.xavi.marvelheroes.domain.utils

interface NetworkClient<Client> {
    fun client(): Client
}

fun interface Predicate<T, R : DTO> {
    fun mapper(): Mapper<T, R>
}
