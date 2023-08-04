package com.rokoblak.routeplanner.data.repo.model

import retrofit2.Response

sealed interface CallResult<out T> {
    data class Success<T>(val value: T): CallResult<T>
    data class Error(val type: LoadErrorType): CallResult<Nothing>

    suspend fun <R>map(mapper: suspend (T) -> R): CallResult<R> = when (this) {
        is Error -> this
        is Success -> Success(mapper(value))
    }

    suspend fun <R>flatMap(mapper: suspend (T) -> CallResult<R>): CallResult<R> = when (this) {
        is Error -> this
        is Success -> mapper(value)
    }

    val optValue get() = (this as? Success)?.value

    companion object {

        fun <T, K, R>compose(first: CallResult<T>, second: CallResult<K>, onSuccess: (T, K) -> R): CallResult<R> {
            val firstValue = when (first) {
                is Error -> return first
                is Success -> first.value
            }
            val secondValue = when (second) {
                is Error -> return second
                is Success -> second.value
            }
            return Success(onSuccess(firstValue, secondValue))
        }

        suspend fun <T> wrappedSafeCall(call: suspend () -> Response<T>): CallResult<T> {
            return try {
                val value = call()
                value.map {
                    it
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Error(LoadErrorType.ApiError(e.message ?: "Api error"))
            }
        }
    }
}

sealed interface LoadErrorType {
    data object NoNetwork: LoadErrorType
    data class ApiError(val message: String): LoadErrorType
}

fun <T, R> Response<T>.map(mapper: (T) -> R): CallResult<R> {
    if (!isSuccessful) return CallResult.Error(LoadErrorType.ApiError(this.message()))
    val body = this.body() ?: return CallResult.Error(LoadErrorType.ApiError("Empty body"))
    return CallResult.Success(mapper(body))
}
