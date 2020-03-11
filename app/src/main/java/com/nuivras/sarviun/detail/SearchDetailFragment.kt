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
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.nuivras.sarviun.BR
import com.nuivras.sarviun.R
import com.nuivras.sarviun.databinding.FragmentSearchDetailBinding
import com.nuivras.sarviun.network.Type
import kotlinx.android.synthetic.main.bottom_sheet_search_detail.*
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



        //jestli se stahlo, zobraz to
        viewModel.propertiesIdentify.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                detail_info_nested_scroll_view.visibility = View.VISIBLE
            }
        })

        //jestli se nestahl stavebni objekt pro adresni misto, zobraz upozorneni
        viewModel.firstIdentify.observe(viewLifecycleOwner, Observer {
            if (viewModel.selectedProperty.value?.feature?.attributes?.typeTranslated == Type.ADRESNI_MISTO
                && it.layerId != 3) { //stavebni objekt
                stavebni_objekt_not_found_layout.visibility = View.VISIBLE
            }
            expandInnerLayout(inflater, it.layerId)
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
                viewModel.getDetails()

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

    private fun expandInnerLayout(inflater: LayoutInflater, layerId: Int) {
        //fintafn pro pridani layoutu i s viewmodelem programove
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            inflater,
            getInnerLayoutId(layerId),
            detail_info_nested_scroll_view,
            true
        )
        binding.setVariable(BR.viewModel, viewModel)
    }

    private fun getInnerLayoutId(layerId: Int) = when (layerId) {
        Type.STAVEBNI_OBJEKT.layerId -> R.layout.detail_stavebni_objekt
//        Type.ULICE.layerId -> R.layout.detail_ulice
        Type.PARCELA.layerId -> R.layout.detail_parcela
//        Type.ZAKLADNI_SIDELNI_JEDNOTKA.layerId -> R.layout.detail_zakladni_sidelni_jednotka
//        Type.KATASTRALNI_UZEMI.layerId -> R.layout.detail_katastralni_uzemi
//        Type.MESTSKY_OBVOD_MESTSKA_CAST.layerId -> R.layout.detail_mestsky_obvod_mestska_cast
//        Type.SPRAVNI_OBVOD_PRAHA.layerId -> R.layout.detail_spravni_obvod_praha
//        Type.CAST_OBCE.layerId -> R.layout.detail_cast_obce
//        Type.OBEC.layerId -> R.layout.detail_obec
//        Type.OBEC_SPOU.layerId -> R.layout.detail_obec_spou
//        Type.OBEC_SROP.layerId -> R.layout.detail_obec_srop
//        Type.OKRES.layerId -> R.layout.detail_okres
//        Type.VYSSI_CELEK.layerId -> R.layout.detail_vyssi_celek
        else -> R.layout.detail_stavebni_objekt
    }

    private fun showObjectPolygon(it: ArrayList<DoubleArray>) {
        val array = arrayOfNulls<DoubleArray>(it.size)
        it.toArray(array)
        webView.loadUrl("javascript:showPolygon(" + array.contentDeepToString() + ") ")
    }

}
