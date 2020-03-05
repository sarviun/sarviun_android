package com.nuivras.sarviun.network.identify


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class IdentifyResponse(
    @Json(name = "results")
    val results: List<Result>
)