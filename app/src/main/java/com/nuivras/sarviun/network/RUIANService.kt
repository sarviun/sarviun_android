package com.nuivras.sarviun.network


import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.nuivras.sarviun.network.identify.IdentifyResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "http://ags.cuzk.cz/arcgis/rest/services/RUIAN/Vyhledavaci_sluzba_nad_daty_RUIAN/MapServer/"

interface RUIANService {
    /**
     * @GET declares an HTTP GET request
     * @Path("user") annotation on the userId parameter marks it as a
     * replacement for the {user} placeholder in the @GET path
     */

    @GET("exts/GeocodeSOE/find")
    suspend fun find(@Query("text") singleLine: String,
                     @Query("f") f: String = "pjson"): FindResponse


    @GET("identify")
    suspend fun identify(@Query("geometry") geometry: String,
                         @Query("mapExtent") mapExtent: String,
                         @Query("layers") layers: String,
                         @Query("returnGeometry") returnGeometry: String = "true",
                         @Query("imageDisplay") imageDisplay: String = "600,550,96",
                         @Query("tolerance") tolerance: String = "5000",
                         @Query("geometryType") geometryType: String = "esriGeometryPoint",
                         @Query("f") f: String = "pjson"): IdentifyResponse


}

/**
 * Build the Moshi object that Retrofit will be using, making sure to add the Kotlin adapter for
 * full Kotlin compatibility.
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

/**
 * Use the Retrofit builder to build a retrofit object.
 */
private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .build()

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object RUIANApi {
    val retrofitService : RUIANService by lazy { retrofit.create(RUIANService::class.java) }
}