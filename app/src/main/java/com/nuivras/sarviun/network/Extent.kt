package com.nuivras.sarviun.network


import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class Extent(
    @Json(name = "xmin")
    val xmin: Double,
    @Json(name = "ymin")
    val ymin: Double,
    @Json(name = "xmax")
    val xmax: Double,
    @Json(name = "ymax")
    val ymax: Double,
    @Json(name = "spatialReference")
    val spatialReference: SpatialReference
): Parcelable {

}