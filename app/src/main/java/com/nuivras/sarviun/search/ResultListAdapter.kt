package com.nuivras.sarviun.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.nativead.NativeAd
import com.nuivras.sarviun.databinding.ListAdViewItemBinding
import com.nuivras.sarviun.databinding.ListViewItemBinding
import com.nuivras.sarviun.network.LocationGeneral


/**
 * This class implements a [RecyclerView] [ListAdapter] which uses Data Binding to present [List]
 * data, including computing diffs between lists.
 * @param onClick a lambda that takes the
 */
private const val LOCATION_TYPE = 10
private const val AD_TYPE = 11

class ResultListAdapter(val onClickListener: OnClickListener) : ListAdapter<Any, ResultListAdapter.BaseViewHolder<*>>(DiffCallback) {


        abstract class BaseViewHolder<T>(binding: ViewDataBinding): RecyclerView.ViewHolder(binding.root) {
            abstract fun bind (item: T)
        }
        /**
         * The LocationViewHolder constructor takes the binding variable from the associated
         * ListViewItem, which nicely gives it access to the full [LocationGeneral] information.
         */
        class LocationViewHolder(private var binding: ListViewItemBinding):
            BaseViewHolder<LocationGeneral> (binding) {
            override fun bind(locationProperty: LocationGeneral) {
                binding.property = locationProperty
                // This is important, because it forces the data binding to execute immediately,
                // which allows the RecyclerView to make the correct view size measurements
                binding.executePendingBindings()
            }
        }

    /**
     * The NativeAdViewHolder constructor takes the binding variable from the associated
     * ListAdViewItem, which nicely gives it access to the full [NaviteAd] information.
     */
    class NativeAdViewHolder(private var binding: ListAdViewItemBinding):
        BaseViewHolder<NativeAd>(binding) {
        override fun bind(nativeAdProperty: NativeAd) {
            binding.property = nativeAdProperty
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            binding.executePendingBindings()
        }
    }

        /**
         * Allows the RecyclerView to determine which items have changed when the [List]
         * of [FindResponse.LocationGeneral]
         * or [NativeAd]
         * has been updated.
         */
        companion object DiffCallback : DiffUtil.ItemCallback<Any>() {
            override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
                return if (oldItem is LocationGeneral && newItem is LocationGeneral)
                    oldItem.extent.spatialReference.wkid == newItem.extent.spatialReference.wkid
                else if (oldItem is NativeAd && newItem is NativeAd)
                    oldItem === newItem
                else false
            }

            override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
                return if (oldItem is LocationGeneral && newItem is LocationGeneral)
                    (oldItem as LocationGeneral) == newItem
                else if (oldItem is NativeAd && newItem is NativeAd)
                    (oldItem as NativeAd).equals(newItem)
                else false
            }
        }

        /**
         * Create new [RecyclerView] item views (invoked by the layout manager)
         */
        override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int): BaseViewHolder<*> {
            return when (viewType) {
                LOCATION_TYPE -> LocationViewHolder(ListViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
                AD_TYPE -> NativeAdViewHolder(ListAdViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
                else -> throw IllegalArgumentException("Invalid type")
            }
        }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is NativeAd -> AD_TYPE
            is LocationGeneral -> LOCATION_TYPE
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

        /**
         * Replaces the contents of a view (invoked by the layout manager)
         */
        override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {

            when (val item = getItem(position)) {
                is LocationGeneral -> {
                    (holder as LocationViewHolder).itemView.setOnClickListener {
                        onClickListener.onClick(item, holder.itemView)
                    }
                    holder.bind(item)
                }
                is NativeAd -> (holder as NativeAdViewHolder).bind(item)
                else -> throw IllegalArgumentException()
            }
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
