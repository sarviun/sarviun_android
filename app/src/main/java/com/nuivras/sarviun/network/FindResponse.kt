package com.nuivras.sarviun.network


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FindResponse(
    @Json(name = "locations")
    val locations: List<LocationGeneral>,
    @Json(name = "spatialReference")
    val spatialReference: SpatialReference
)