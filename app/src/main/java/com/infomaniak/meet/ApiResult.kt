package com.infomaniak.meet

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val result: Status = Status.unknown,
    val data: T? = null,
    val error: ApiError? = null
) {
    @Serializable
    enum class Status {
        error,
        success,
        asynchronous,
        unknown;
    }
}
