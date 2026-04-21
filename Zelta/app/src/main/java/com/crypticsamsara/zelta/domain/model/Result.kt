package com.crypticsamsara.zelta.domain.model



sealed class ZeltaResult<out T> {

    data class Success<T>(val data: T) : ZeltaResult<T>()

    data class Error(
        val message: String,
        val cause: Throwable? = null
    ) : ZeltaResult<Nothing>()

    object Loading : ZeltaResult<Nothing>()
}

// Extension Helpers
inline fun <T> ZeltaResult<T>.onSuccess(action: (T) -> Unit): ZeltaResult<T> {
    if (this is ZeltaResult.Success) action(data)
    return this
}

inline fun <T> ZeltaResult<T>.onError(action: (String, Throwable?) -> Unit): ZeltaResult<T> {
    if (this is ZeltaResult.Error) action(message, cause)
    return this
}

inline fun <T> ZeltaResult<T>.onLoading(action: () -> Unit): ZeltaResult<T> {
    if (this is ZeltaResult.Loading) action()
    return this
}

// safe execution wrapper
suspend fun <T> safeCall(
    action: suspend () -> T
): ZeltaResult<T> = try {
    ZeltaResult.Success(action())
} catch (e: Exception) {
    ZeltaResult.Error(
        message = e.message ?: "Something went wrong",
        cause = e)
}


