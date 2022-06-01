package com.xavi.marvelheroes.domain.utils

interface DTO

interface Mapper<T, R> {
    fun map(dto: R): T
}

interface DBMapper<DOMAIN, DB> {
    fun mapToDomain(dbModel: DB): DOMAIN
    fun mapToDB(domainModel: DOMAIN): DB
}

sealed class State<out T> {
    data class Error(val error: Throwable) : State<Nothing>()
    data class Success<out T>(val data: T) : State<T>()

    val isSuccess get() = this is Success<T>
    val isFailure get() = this is Error

    companion object {
        fun <T> success(data: T) = Success(data)
        fun error(error: Throwable) = Error(error)
    }
}

fun <T> List<State<T>>.flat(): State<List<T>> {
    val list = mutableListOf<T>()
    this.takeWhile {
        when (it) {
            is State.Error -> return State.error(it.error)
            is State.Success -> {
                list.add(it.data)
                true
            }
        }
    }
    return State.Success(list)
}
