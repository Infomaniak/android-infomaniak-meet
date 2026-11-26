package com.infomaniak.meet

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
class ApiError(
    val code: String? = null,
    val description: String? = null,
    val context: JsonObject? = null,
    val errors: Array<ApiError>? = null
)