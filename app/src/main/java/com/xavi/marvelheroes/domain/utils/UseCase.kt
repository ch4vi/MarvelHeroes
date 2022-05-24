package com.xavi.marvelheroes.domain.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

abstract class UseCase<Type, in Params> where Type : Any? {
    abstract fun run(params: Params): Flow<Type>

    @JvmOverloads
    operator fun invoke(
        params: Params,
        dispatcher: CoroutineDispatcher = Dispatchers.Default
    ) = flow {
        emitAll(run(params))
    }.flowOn(dispatcher)
}

operator fun <Type> UseCase<Type, Unit>.invoke() = this.invoke(params = Unit)
