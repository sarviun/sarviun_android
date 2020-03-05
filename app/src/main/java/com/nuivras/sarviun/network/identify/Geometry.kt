package com.nuivras.sarviun.network.identify


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Geometry(
    @Json(name = "rings")
    val rings: List<List<Any>>,
    @Json(name = "spatialReference")
    val spatialReference: SpatialReference
)