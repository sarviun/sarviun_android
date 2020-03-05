package com.nuivras.sarviun.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nuivras.sarviun.databinding.ListViewItemBinding
import com.nuivras.sarviun.network.LocationGeneral


/**
 * This class implements a [RecyclerView] [ListAdapter] which uses Data Binding to present [List]
 * data, including computing diffs between lists.
 * @param onClick a lambda that takes the
 */

class ResultListAdapter(val onClickListener: OnClickListener) : ListAdapter<LocationGeneral, ResultListAdapter.LocationViewHolder>(DiffCallback) {


        /**
         * The LocationViewHolder constructor takes the binding variable from the associated
         * ListViewItem, which nicely gives it access to the full [LocationGeneral] information.
         */
        class LocationViewHolder(private var binding: ListViewItemBinding):
            RecyclerView.ViewHolder(binding.root) {
            fun bind(locationProperty: LocationGeneral) {
                binding.property = locationProperty
                // This is important, because it forces the data binding to execute immediately,
                // which allows the RecyclerView to make the correct view size measurements
                binding.executePendingBindings()
            }
        }

        /**
         * Allows the RecyclerView to determine which items have changed when the [List] of [FindResponse.LocationGeneral]
         * has been updated.
         */
        companion object DiffCallback : DiffUtil.ItemCallback<LocationGeneral>() {
            override fun areItemsTheSame(oldItem: LocationGeneral, newItem: LocationGeneral): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: LocationGeneral, newItem: LocationGeneral): Boolean {
                return oldItem.extent.spatialReference.wkid == newItem.extent.spatialReference.wkid
            }
        }

        /**
         * Create new [RecyclerView] item views (invoked by the layout manager)
         */
        override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int): LocationViewHolder {
            return LocationViewHolder(ListViewItemBinding.inflate(LayoutInflater.from(parent.context)))
        }

        /**
         * Replaces the contents of a view (invoked by the layout manager)
         */
        override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
            val location = getItem(position)
            holder.itemView.setOnClickListener {
                onClickListener.onClick(location, holder.itemView)
            }
            holder.bind(location)
        }

        /**
         * Custom listener that handles clicks on [RecyclerView] items.  Passes the [FindResponse.LocationGeneral]
         * associated with the current item to the [onClick] function.
         * @param clickListener lambda that will be called with the current [FindResponse.LocationGeneral]
         */
        class OnClickListener(val clickListener: (location: LocationGeneral, view: View) -> Unit) {
            fun onClick(location: LocationGeneral, view: View) = clickListener(location, view)
        }


}
