package com.nuivras.sarviun

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nuivras.sarviun.network.LocationGeneral
import com.nuivras.sarviun.search.ResultListAdapter

/**
 * When there is no Mars property data (data is null), hide the [RecyclerView], otherwise show it.
 */
@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<LocationGeneral>?) {
    val adapter = recyclerView.adapter as ResultListAdapter
    adapter.submitList(data)
}