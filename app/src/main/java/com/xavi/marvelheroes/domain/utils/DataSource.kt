package com.xavi.marvelheroes.domain.utils

import com.xavi.marvelheroes.domain.model.ErrorDomainModel
import com.xavi.marvelheroes.domain.model.Failure

interface DataSource<Client, out T, R : DTO, in P> {

    val networkClient: NetworkClient<Client>

    suspend fun fetch(predicate: P): State<T>

    fun errorHandler(e: ErrorDomainModel): Throwable {
        return Failure.ResponseError(e)
    }
}
