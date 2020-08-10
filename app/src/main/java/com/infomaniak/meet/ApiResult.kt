package com.infomaniak.meet

import kotlinx.android.parcel.RawValue

data class ApiResponse<T>(
    val result: Status = Status.unknown,
    val data: @RawValue T? = null,
    val error: ApiError? = null
) {
    enum class Status {
        error,
        success,
        asynchronous,
        unknown;
    }
}