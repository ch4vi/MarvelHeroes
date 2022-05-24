package com.xavi.marvelheroes.domain.utils

import com.xavi.marvelheroes.domain.model.ErrorDomainModel
import com.xavi.marvelheroes.domain.model.Failure

abstract class DataSource<Client, out T, R : DTO, in P>(networkClient: NetworkClient<Client>) {

    val client: Client = networkClient.client()

    abstract suspend fun fetch(predicate: P): State<T>

    open fun errorHandler(e: ErrorDomainModel): Throwable {
        return Failure.ResponseError(e)
    }
}
