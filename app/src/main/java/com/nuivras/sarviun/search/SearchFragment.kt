package com.nuivras.sarviun.search

import android.os.Build
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.transition.TransitionInflater
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.nuivras.sarviun.MainActivity

import com.nuivras.sarviun.databinding.FragmentSearchBinding
import com.nuivras.sarviun.network.LocationGeneral
import com.nuivras.sarviun.utils.Utils
import kotlinx.android.synthetic.main.list_view_item.view.*

class SearchFragment : Fragment() {


    var sharedElement: View? = null
    var query: CharSequence = ""

    /**
     * Lazily initialize our [SearchViewModel].
     */
    private val viewModel: SearchViewModel by lazy {
        ViewModelProviders.of(this).get(SearchViewModel::class.java)
    }


    /**
     * Inflates the layout with Data Binding, sets its lifecycle owner to the OverviewFragment
     * to enable Data Binding to observe LiveData, and sets up the RecyclerView with an adapter.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentSearchBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.setLifecycleOwner(this)

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


        binding.editText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                query = s
                if (query.isEmpty()) {
                    binding.typeToSearch.visibility = View.VISIBLE
                }
                else {
                    binding.typeToSearch.visibility = View.GONE
                }
                viewModel.updateLocationResults(s.toString())

            }
        })

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        }
    }
}
