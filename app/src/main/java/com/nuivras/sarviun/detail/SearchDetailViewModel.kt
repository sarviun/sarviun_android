package com.nuivras.sarviun.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngQuad
import com.nuivras.sarviun.network.ExportResponse
import com.nuivras.sarviun.network.LocationGeneral
import com.nuivras.sarviun.network.RUIANApi
import com.nuivras.sarviun.network.Type
import com.nuivras.sarviun.network.identify.Result
import com.nuivras.sarviun.search.RUIANApiStatus
import com.nuivras.sarviun.utils.CoordinatesConvertor
import kotlinx.coroutines.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.absoluteValue


class SearchDetailViewModel (locationProperty: LocationGeneral?, app: Application) :
    AndroidViewModel(app) {

    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<RUIANApiStatus>()

    // The external immutable LiveData for the request status
    val status: LiveData<RUIANApiStatus>
        get() = _status

    // Internally, we use a MutableLiveData, because we will be updating the List of MarsProperty
    // with new values
    private val _propertiesIdentify = MutableLiveData<List<Result>>()

    // The external LiveData interface to the property is immutable, so only this class can modify
    val propertiesIdentify: LiveData<List<Result>>
        get() = _propertiesIdentify

    // Internally, we use a MutableLiveData, because we will be updating the List of MarsProperty
    // with new values
    private val _firstIdentify = MutableLiveData<Result>()

    // The external LiveData interface to the property is immutable, so only this class can modify
    val firstIdentify: LiveData<Result>
        get() = _firstIdentify

    // Internally, we use a MutableLiveData, because we will be updating the List of MarsProperty
    // with new values
    private val _polygonsCoordinates = MutableLiveData<ArrayList<ArrayList<DoubleArray>>>()

    // The external LiveData interface to the property is immutable, so only this class can modify
    val polygonsCoordinates: LiveData<ArrayList<ArrayList<DoubleArray>>>
        get() = _polygonsCoordinates

    // Internally, we use a MutableLiveData, because we will be updating the List of MarsProperty
    // with new values
    private val _propertyMapExport = MutableLiveData<ExportResponse>()

    // The external LiveData interface to the property is immutable, so only this class can modify
    val propertyMapExport: LiveData<ExportResponse>
        get() = _propertyMapExport

    private val _selectedProperty = MutableLiveData<LocationGeneral>()

    // The external LiveData for the SelectedProperty
    val selectedProperty: LiveData<LocationGeneral>
        get() = _selectedProperty

    // Initialize the _selectedProperty MutableLiveData
    init {
        _selectedProperty.value = locationProperty
    }

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private var coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private fun identify (geometry: String, mapExtent: String) {
        coroutineScope.launch {

            try {
                _status.value = RUIANApiStatus.LOADING
                // this will run on a thread managed by Retrofit
                val listResult = RUIANApi.retrofitService
                    .identify(geometry, mapExtent, getLayerTypes())
                _propertiesIdentify.value = listResult.results

                if (listResult.results.isNotEmpty()) {
                    _firstIdentify.value = _propertiesIdentify.value?.get(0)
                    //transformShape(_firstIdentify.value?.geometry?.rings?.get(0)!!)
                    val polygonsGeometries = arrayListOf<ArrayList<DoubleArray>>()

                    for (array in _firstIdentify.value?.geometry?.rings!!)
                        polygonsGeometries.add(fetchTransformedCoordinates(array))

                    _polygonsCoordinates.value = polygonsGeometries
                }

                _status.value = RUIANApiStatus.DONE

            } catch (e: Exception) {
                //if viewmodeljob is canceled in updateLocationResult,
                //exception is raising, need to filter the reason of the exception
                if (_status.value != RUIANApiStatus.RESTARTED) {
                    _status.value = RUIANApiStatus.ERROR
                    _propertiesIdentify.value = ArrayList()
                }
            }
        }
    }

    //TODO: cisla nahradit Type.layerId
    private fun getLayerTypes(): String {
        val number = when (_selectedProperty.value?.feature?.attributes?.typeTranslated) {
            Type.PARCELA_DEFINICNI_BOD -> "5" // parcela
            Type.ADRESNI_MISTO -> "3,5" //stavebni objekt, parcela
            Type.ULICE -> "4"
            Type.ZAKLADNI_SIDELNI_JEDNOTKA -> "6"
            Type.KATASTRALNI_UZEMI -> "7"
            Type.MESTSKY_OBVOD_MESTSKA_CAST -> "8"
            Type.SPRAVNI_OBVOD_PRAHA -> "10"
            Type.CAST_OBCE -> "11"
            Type.OBEC -> "12"
            Type.OBEC_SPOU -> "13"
            Type.OBEC_SROP -> "14"
            Type.OKRES -> "15"
            Type.VYSSI_CELEK -> "17"
            else -> "5"
        }
        return "all:$number"
    }


    private suspend fun fetchTransformedCoordinates(listOfPoints: List<Any>): ArrayList<DoubleArray> {
        return GlobalScope.async(Dispatchers.Default) {
            val transformedCoordinates = arrayListOf<DoubleArray>()
            for (point in listOfPoints) {
                if (point is List<*>) {
                    val pointInDouble: List<Double> = point.filterIsInstance<Double>()
                    transformedCoordinates.add(CoordinatesConvertor.JTSKtoWGS(pointInDouble[1].absoluteValue, pointInDouble[0].absoluteValue, 245.0))
                }
            }
            return@async transformedCoordinates
        }.await()
    }

    fun getDetails () {
        val locale = Locale("en", "UK")
        val pattern = "#.##"

        val df =
            NumberFormat.getNumberInstance(locale) as DecimalFormat
        df.applyPattern(pattern)
        df.roundingMode = RoundingMode.CEILING




        val geometry = df.format(_selectedProperty.value?.feature?.geometry?.x).toString() +
                "," + df.format(_selectedProperty.value?.feature?.geometry?.y).toString()

        val mapExtent = _selectedProperty.value?.feature?.attributes?.xmin.toString() +
                "," + _selectedProperty.value?.feature?.attributes?.ymin.toString() +
                "," + _selectedProperty.value?.feature?.attributes?.xmax.toString() +
                "," + _selectedProperty.value?.feature?.attributes?.ymax.toString()

        identify(geometry, mapExtent)
    }

    /**
     * When the [ViewModel] is finished, we cancel our coroutine [viewModelJob], which tells the
     * Retrofit service to stop.
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun getMapImage(
        southWest: LatLng,
        northEast: LatLng,
        height: Float,
        width: Float,
        dpi: Int
    ) {
        val bbox = southWest.longitude.toString() +
                "," + southWest.latitude.toString() +
                "," + northEast.longitude.toString() +
                "," + northEast.latitude.toString()

        val size = "$width,$height"

        export(bbox, size, dpi.toString())
    }

    private fun export(bbox: String, size: String, dpi: String) {
        coroutineScope.launch {

            try {
                // this will run on a thread managed by Retrofit
                val exportResult = RUIANApi.retrofitService
                    .export(bbox,size,dpi)
                _propertyMapExport.value = exportResult

            } catch (e: Exception) {
                //if viewmodeljob is canceled in updateLocationResult,
                //exception is raising, need to filter the reason of the exception

            }
        }
    }
}
