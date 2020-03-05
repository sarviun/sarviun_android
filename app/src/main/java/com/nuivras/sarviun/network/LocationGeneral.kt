package com.nuivras.sarviun.network


import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class LocationGeneral(
    @Json(name = "name")
    val name: String,
    @Json(name = "extent")
    val extent: Extent,
    @Json(name = "feature")
    val feature: Feature
) : Parcelable {
}