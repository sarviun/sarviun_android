package com.nuivras.sarviun.network


import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class Geometry(
    @Json(name = "x")
    val x: Double,
    @Json(name = "y")
    val y: Double,
    @Json(name = "spatialReference")
    val spatialReference: SpatialReference
): Parcelable {

}