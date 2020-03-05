package com.nuivras.sarviun.network


import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class Feature(
    @Json(name = "geometry")
    val geometry: Geometry,
    @Json(name = "attributes")
    val attributes: Attributes
): Parcelable {

}