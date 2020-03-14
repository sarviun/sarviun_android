package com.nuivras.sarviun

import android.view.View
import android.widget.ProgressBar
import androidx.core.widget.NestedScrollView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nuivras.sarviun.network.LocationGeneral
import com.nuivras.sarviun.search.RUIANApiStatus
import com.nuivras.sarviun.search.ResultListAdapter

/**
 * When there is no Mars property data (data is null), hide the [RecyclerView], otherwise show it.
 */
@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<LocationGeneral>?) {
    val adapter = recyclerView.adapter as ResultListAdapter
    adapter.submitList(data)
}

///**
// * This binding adapter displays the [MarsApiStatus] of the network request in an image view.  When
// * the request is loading, it displays a loading_animation.  If the request has an error, it
// * displays a broken image to reflect the connection error.  When the request is finished, it
// * hides the image view.
// */
@BindingAdapter("loadingVisibility")
fun bindStatus(statusProgressBar: ProgressBar, status: RUIANApiStatus?) {
    when (status) {
        RUIANApiStatus.LOADING -> {
            statusProgressBar.visibility = View.VISIBLE
        }
        else -> {
            statusProgressBar.visibility = View.GONE
        }
    }
}


