package com.nuivras.sarviun.network.identify


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Result(
    @Json(name = "layerId")
    val layerId: Int,
    @Json(name = "layerName")
    val layerName: String,
    @Json(name = "displayFieldName")
    val displayFieldName: String,
    @Json(name = "value")
    val value: String,
    @Json(name = "attributes")
    val attributes: Attributes,
    @Json(name = "geometryType")
    val geometryType: String,
    @Json(name = "geometry")
    val geometry: Geometry
)