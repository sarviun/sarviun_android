package com.nuivras.sarviun.detail

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nuivras.sarviun.network.LocationGeneral

class SearchDetailViewModelFactory(private val location: LocationGeneral,
                                   private val application: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchDetailViewModel::class.java)) {
            return SearchDetailViewModel(location, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
