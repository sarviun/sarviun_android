package com.nuivras.sarviun.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
import com.nuivras.sarviun.network.LocationGeneral
import com.nuivras.sarviun.network.RUIANApi
import kotlinx.coroutines.*

enum class RUIANApiStatus { LOADING, ERROR, DONE, RESTARTED }

class SearchViewModel : ViewModel() {

    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<RUIANApiStatus>()

    // The external immutable LiveData for the request status
    val status: LiveData<RUIANApiStatus>
        get() = _status

    // Internally, we use a MutableLiveData, because we will be updating the List of MarsProperty
    // with new values
    private val _properties = MutableLiveData<List<LocationGeneral>>()

    // The external LiveData interface to the property is immutable, so only this class can modify
    val properties: LiveData<List<LocationGeneral>>
        get() = _properties

    // Internally, we use a MutableLiveData to handle navigation to the selected property
    private val _navigateToSelectedProperty = MutableLiveData<LocationGeneral>()

    // The external immutable LiveData for the navigation property
    val navigateToSelectedProperty: LiveData<LocationGeneral>
        get() = _navigateToSelectedProperty

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private var coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private fun getLocations (query: String) {
        coroutineScope.launch {

            try {
                _status.value = RUIANApiStatus.LOADING
                // this will run on a thread managed by Retrofit
                val listResult = RUIANApi.retrofitService
                    .find(query)
                _status.value = RUIANApiStatus.DONE
                _properties.value = listResult.locations
            } catch (e: Exception) {
                //if viewmodeljob is canceled in updateLocationResult,
                //exception is raising, need to filter the reason of the exception
                if (_status.value != RUIANApiStatus.RESTARTED) {
                    _status.value = RUIANApiStatus.ERROR
                    _properties.value = ArrayList()
                }
            }
        }
    }

    fun updateLocationResults (query: String) {
        if (_status.value == RUIANApiStatus.LOADING) {
            cancelRequest()
        }
        getLocations(query)
    }

    private fun cancelRequest() {
        _status.value = RUIANApiStatus.RESTARTED
        viewModelJob.cancel()
        viewModelJob = Job()
        coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)
    }

    /**
     * When the [ViewModel] is finished, we cancel our coroutine [viewModelJob], which tells the
     * Retrofit service to stop.
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    /**
     * When the property is clicked, set the [_navigateToSelectedProperty] [MutableLiveData]
     * @param locationProperty The [FindResponse.LocationGeneral] that was clicked on.
     */
    fun displayPropertyDetails(locationProperty: LocationGeneral) {
        _navigateToSelectedProperty.value = locationProperty
    }

    /**
     * After the navigation has taken place, make sure navigateToSelectedProperty is set to null
     */
    fun displayPropertyDetailsComplete() {
        _navigateToSelectedProperty.value = null
    }

}
