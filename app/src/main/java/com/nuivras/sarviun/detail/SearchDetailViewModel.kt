package com.nuivras.sarviun.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nuivras.sarviun.network.LocationGeneral

class SearchDetailViewModel (locationProperty: LocationGeneral, app: Application) :
    AndroidViewModel(app) {

    private val _selectedProperty = MutableLiveData<LocationGeneral>()

    // The external LiveData for the SelectedProperty
    val selectedProperty: LiveData<LocationGeneral>
        get() = _selectedProperty

    // Initialize the _selectedProperty MutableLiveData
    init {
        _selectedProperty.value = locationProperty
    }

//    val convertToCzech = Transformations.map(selectedProperty) {
//        app.applicationContext.getString(
//            convertToCzech(it.attributes.type))
//    }
}
