package com.xavi.marvelheroes.domain.utils

open class Repository<Client, T, R, P>(
    private val dataSource: DataSource<Client, T, R, P>,
) where
R : DTO,
P : Predicate<T, R> {

    suspend fun fetch(predicate: P): State<T> {
        return fetchFromNetwork(predicate)
    }

    private suspend fun fetchFromNetwork(predicate: P): State<T> {
        return dataSource.fetch(predicate)
    }
}
