package com.rokoblak.routeplanner.data.repo.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

sealed interface LoadableResult<out T> {
    data class Success<T>(val value: T) : LoadableResult<T>
    data class Error(val type: LoadErrorType) : LoadableResult<Nothing>
    data object Loading : LoadableResult<Nothing>

    suspend fun <R> map(mapper: suspend (T) -> R): LoadableResult<R> = when (this) {
        is Error -> this
        is Success -> Success(mapper(value))
        is Loading -> Loading
    }

    fun <T, R, C> Flow<LoadableResult<T>>.concatOnSuccess(
        other: (T) -> Flow<LoadableResult<R>>,
        mapper: (T, R?, LoadableResult<R>) -> C
    ): Flow<LoadableResult<C>> {
        return flatMapConcat { res ->
            when (res) {
                is Error -> flowOf(res)
                Loading -> flowOf(Loading)
                is Success -> {
                    val routingDetailsFlow = other(res.value)
                    routingDetailsFlow.map { routingRes ->
                        when (routingRes) {
                            is Error -> mapper(res.value, null, routingRes)
                            Loading -> mapper(res.value, null, routingRes)
                            is Success -> mapper(res.value, routingRes.value, routingRes)
                        }
                    }.map {
                        Success(it)
                    }
                }
            }
        }
    }
}

fun <T> CallResult<T>.toLoadable(): LoadableResult<T> = when (this) {
    is CallResult.Error -> LoadableResult.Error(type)
    is CallResult.Success -> LoadableResult.Success(value)
}
