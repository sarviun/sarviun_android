package com.nuivras.sarviun.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nuivras.sarviun.network.LocationGeneral
import com.nuivras.sarviun.network.RUIANApi
import com.nuivras.sarviun.network.identify.Result
import com.nuivras.sarviun.search.RUIANApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


class SearchDetailViewModel (locationProperty: LocationGeneral, app: Application) :
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
                    .identify(geometry, mapExtent)
                _status.value = RUIANApiStatus.DONE
                _propertiesIdentify.value = listResult.results
                _firstIdentify.value = propertiesIdentify.value?.get(0)
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

    fun getDetails () {
        val locale = Locale("en", "UK")
        val pattern = "###.##"

        val df =
            NumberFormat.getNumberInstance(locale) as DecimalFormat
        df.applyPattern(pattern)
        df.roundingMode = RoundingMode.CEILING



        val geometry = _selectedProperty.value?.feature?.geometry?.x?.roundToInt().toString() + "," + _selectedProperty.value?.feature?.geometry?.y?.roundToInt().toString()
        val mapExtent = df.format(_selectedProperty.value?.feature?.attributes?.xmin).toString() + "," + df.format(_selectedProperty.value?.feature?.attributes?.ymin).toString() + "," + df.format(_selectedProperty.value?.feature?.attributes?.xmax).toString() + "," + df.format(_selectedProperty.value?.feature?.attributes?.ymax).toString()
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
}
