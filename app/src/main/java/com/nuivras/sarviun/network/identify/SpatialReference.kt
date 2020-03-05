package com.nuivras.sarviun.network.identify


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SpatialReference(
    @Json(name = "wkid")
    val wkid: Int,
    @Json(name = "latestWkid")
    val latestWkid: Int
)