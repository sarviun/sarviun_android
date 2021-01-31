package com.nuivras.sarviun.search

import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.*
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.nuivras.sarviun.MainActivity
import com.nuivras.sarviun.R

import com.nuivras.sarviun.databinding.FragmentSearchBinding
import com.nuivras.sarviun.databinding.ListAdViewItemBinding
import com.nuivras.sarviun.network.LocationGeneral
import com.nuivras.sarviun.utils.Utils
import kotlinx.android.synthetic.main.list_view_item.view.*

class SearchFragment : Fragment() {


    var sharedElement: View? = null
    var query: CharSequence = ""
    lateinit var mTypeToSearchLayout: LinearLayout
    lateinit var mNotFoundLayout: LinearLayout

    /**
     * Lazily initialize our [SearchViewModel].
     */
    private val viewModel: SearchViewModel by lazy {
        ViewModelProvider(this).get(SearchViewModel::class.java)
    }


    /**
     * Inflates the layout with Data Binding, sets its lifecycle owner to the OverviewFragment
     * to enable Data Binding to observe LiveData, and sets up the RecyclerView with an adapter.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentSearchBinding.inflate(inflater)
        val adBinding = ListAdViewItemBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        // Giving the binding access to the SearchViewModel
        binding.viewModel = viewModel

        // Sets the adapter of the RecyclerView with clickHandler lambda that
        // tells the viewModel when our property is clicked
        binding.listView.adapter = ResultListAdapter(ResultListAdapter.OnClickListener {
                location: LocationGeneral, view: View ->

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        sharedElement = null
                        sharedElement = view
                    }

                    Utils.hideKeyboard(activity as MainActivity)

                    viewModel.displayPropertyDetails(location)

        })

        // Observe the navigateToSelectedProperty LiveData and Navigate when it isn't null
        // After navigating, call displayPropertyDetailsComplete() so that the ViewModel is ready
        // for another navigation event.
        viewModel.navigateToSelectedProperty.observe(viewLifecycleOwner, Observer {
            if ( null != it && sharedElement != null) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    sharedElement!!.address_result.transitionName = "textView"
                    sharedElement!!.imageView2.transitionName = "imageView"
                }

                val extras = FragmentNavigatorExtras(
                    sharedElement!!.address_result to "textView",
                    sharedElement!!.imageView2 to "imageView")
                // Must find the NavController from the Fragment
                this.findNavController().navigate(SearchFragmentDirections.actionShowDetail(it), extras)
                // Tell the ViewModel we've made the navigate call to prevent multiple navigation
                viewModel.displayPropertyDetailsComplete()

            }
        })

        viewModel.properties.observe(viewLifecycleOwner, Observer {
                if (it.isEmpty() && query.isNotEmpty())
                    binding.notFoundLayout.visibility = View.VISIBLE
                else
                    binding.notFoundLayout.visibility = View.GONE

        })

        //ad
        viewModel.prefetchAd(context)

        mTypeToSearchLayout = binding.typeToSearch
        mNotFoundLayout = binding.notFoundLayout

        setHasOptionsMenu(true)

        return binding.root
    }

    //nutny zlo
    private fun setAdViewLayout(binding: ListAdViewItemBinding) {
        binding.nativeAddViewRoot.headlineView = binding.headline
        binding.nativeAddViewRoot.bodyView = binding.description
        binding.nativeAddViewRoot.iconView = binding.imageView2
        binding.nativeAddViewRoot.callToActionView = binding.action
        binding.nativeAddViewRoot.advertiserView = binding.advertiser
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search_detail, menu)

        val mSearch = menu.findItem(R.id.search)
        val mSearchView: SearchView = mSearch.actionView as SearchView
        mSearchView.queryHint = getString(R.string.search)

        mSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    query = newText
                    if (query.isEmpty()) {
                        mTypeToSearchLayout.visibility = View.VISIBLE
                    } else {
                        mTypeToSearchLayout.visibility = View.GONE
                        mNotFoundLayout.visibility = View.GONE
                    }
                    viewModel.updateLocationResults(newText)
                }
                return true
            }
        })

        mSearch.setOnActionExpandListener (
            object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                    return true
                }

                override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                    activity?.supportFragmentManager?.popBackStack()
                    return true
                }
            }
        )

        menu.performIdentifierAction(R.id.search, 0)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.destroyAdd()
    }
}
