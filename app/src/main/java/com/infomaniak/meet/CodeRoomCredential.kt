package com.infomaniak.meet

import kotlinx.serialization.Serializable

@Serializable
class CodeRoomCredential(val name: String, val code: String, val hostname: String)