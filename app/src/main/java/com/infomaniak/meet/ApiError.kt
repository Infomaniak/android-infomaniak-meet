package com.infomaniak.meet

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

class ApiError(
    @SerializedName("code")
    val code: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("context")
    val context: JsonObject? = null,
    @SerializedName("errors")
    val errors: Array<ApiError>? = null
)