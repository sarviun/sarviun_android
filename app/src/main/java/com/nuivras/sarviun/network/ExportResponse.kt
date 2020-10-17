package com.nuivras.sarviun.network


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExportResponse(
    @Json(name = "href")
    val href: String?,
    @Json(name = "width")
    val width: Int?,
    @Json(name = "height")
    val height: Int?,
    @Json(name = "extent")
    val extent: Extent?,
    @Json(name = "scale")
    val scale: Double?
) {

}