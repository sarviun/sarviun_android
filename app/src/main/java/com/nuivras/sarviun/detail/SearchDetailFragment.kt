package com.nuivras.sarviun.detail

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.*
import androidx.fragment.app.Fragment
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.nuivras.sarviun.R
import com.nuivras.sarviun.databinding.FragmentSearchDetailBinding
import com.nuivras.sarviun.network.Type
import kotlinx.android.synthetic.main.bottom_sheet_search_detail.view.*
import kotlinx.android.synthetic.main.fragment_search.view.*
import kotlinx.android.synthetic.main.fragment_search_detail.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


class SearchDetailFragment : Fragment() {

    lateinit var coordinates : String
    lateinit var mapIntent : Intent

    /**
     * Lazily initialize our [SearchDetailViewModel].
     */
    private val viewModel: SearchDetailViewModel by lazy {
        ViewModelProviders.of(this).get(SearchDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)

        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search_detail, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.navigate -> {
                startActivity(mapIntent)
                Toast.makeText(context,"Klik",Toast.LENGTH_SHORT).show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val application = requireNotNull(activity).application
        val binding = FragmentSearchDetailBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val locationProperty = SearchDetailFragmentArgs.fromBundle(arguments!!).selectedProperty
        val viewModelFactory = SearchDetailViewModelFactory(locationProperty, application)
        binding.viewModel = ViewModelProviders.of(
            this, viewModelFactory).get(SearchDetailViewModel::class.java)


        //jestli se nic nestahlo, schovej je, pripadne se prida vysvetleni
        viewModel.propertiesIdentify.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                binding.coordinateBottomSheet.detail_info_nested_scroll_view.visibility = View.VISIBLE
            }
            else {
                binding.coordinateBottomSheet.detail_info_nested_scroll_view.visibility = View.GONE
                if (viewModel.selectedProperty.value?.feature?.attributes?.typeTranslated == Type.ADRESNI_MISTO) {
                    binding.coordinateBottomSheet.stavebni_objekt_not_found_layout.visibility = View.VISIBLE
                }
            }
        })

        //data stazena, jakmile se naplni prekonvertovany shape s parcelou, zobraz polygon v mape
        viewModel.coordinates.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty())
                showObjectPolygon(it)
        })



        //enables javascript in webview
        binding.webView.settings.javaScriptEnabled = true

        if (locationProperty.feature.attributes.isPoint) {
            val longitude =
                (locationProperty.feature.attributes.ymin + locationProperty.feature.attributes.ymax) / 2
            val latitude =
                (locationProperty.feature.attributes.xmax + locationProperty.feature.attributes.xmin) / 2

            coordinates = "$longitude,$latitude"

            // Build the intent
            val location = Uri.parse("geo:$coordinates?z=14")
            mapIntent = Intent(Intent.ACTION_VIEW, location)

            // Verify it resolves
            val activities: List<ResolveInfo> = activity!!.packageManager.queryIntentActivities(mapIntent, 0)
            val isIntentSafe: Boolean = activities.isNotEmpty()

            // Show button in toolbar if it's safe
            if (isIntentSafe) {
                setHasOptionsMenu(true)
            }
        }

        binding.webView.webViewClient = object: WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                view?.loadUrl("javascript:centerToArea(" + locationProperty.feature.attributes.xmin + ", " +
                        locationProperty.feature.attributes.ymin + ", " +
                        locationProperty.feature.attributes.xmax + ", " +
                        locationProperty.feature.attributes.ymax + ", " +
                        locationProperty.feature.attributes.isPoint + ") ")

                super.onPageFinished(view, url)
            }
        }

        //loads sample html
        binding.webView.loadUrl("file:///android_asset/map.html")

        binding.coordinateBottomSheet.katastrButton.setOnClickListener {
            val webpage: Uri = Uri.parse("http://nahlizenidokn.cuzk.cz/MapaIdentifikace.aspx?l=KN&x=${locationProperty.feature.geometry.x.roundToInt()}&y=${locationProperty.feature.geometry.y.roundToInt()}")
            val intent = Intent(Intent.ACTION_VIEW, webpage)
            if (intent.resolveActivity(activity!!.packageManager) != null) {
                startActivity(intent)
            }
        }

        binding.coordinateBottomSheet.emptyDetailButtonExplanation.setOnClickListener {
            //Inflate the dialog with custom view
            val mDialogView = layoutInflater.inflate(R.layout.dialog_explanation_adrress_place, null)
            //AlertDialogBuilder
            val mBuilder = AlertDialog.Builder(context!!)
                .setView(mDialogView)
                .setTitle(getString(R.string.title_explanation_dialog))
                .setPositiveButton(android.R.string.ok) { dialog, _ ->
                    dialog.dismiss()
                }
            //show dialog
            mBuilder.show()
        }

        return binding.root
    }

    private fun showObjectPolygon(it: ArrayList<DoubleArray>) {
        val array = arrayOfNulls<DoubleArray>(it.size)
        it.toArray(array)
        webView.loadUrl("javascript:showPolygon(" + array.contentDeepToString() + ") ")
    }

    override fun onResume() {
        super.onResume()
        viewModel.getDetails()
    }

}
