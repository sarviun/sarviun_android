package com.nuivras.sarviun.network


import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SpatialReference(
    @Json(name = "wkid")
    val wkid: Int,
    @Json(name = "latestWkid")
    val latestWkid: Int
) : Parcelable {

}