package com.nuivras.sarviun.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.DisplayMetrics
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.geometry.LatLngQuad
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.localization.LocalizationPlugin
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.layers.RasterLayer
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.mapboxsdk.style.sources.ImageSource
import com.nuivras.sarviun.BR
import com.nuivras.sarviun.R
import com.nuivras.sarviun.databinding.FragmentSearchDetailBinding
import com.nuivras.sarviun.network.Extent
import com.nuivras.sarviun.network.Type
import com.nuivras.sarviun.utils.CoordinatesConvertor
import kotlinx.android.synthetic.main.bottom_sheet_search_detail.*
import kotlinx.android.synthetic.main.bottom_sheet_search_detail.view.*
import kotlinx.android.synthetic.main.fragment_search_detail.*
import java.net.URI
import kotlin.math.roundToInt

private const val ID_IMAGE_SOURCE = "ID_IMAGE_SOURCE"
private const val ID_IMAGE_LAYER = "ID_IMAGE_LAYER"

class SearchDetailFragment : Fragment() {

    lateinit var coordinates: String
    lateinit var mapIntent: Intent
    lateinit var mMapboxMap: MapboxMap
    private var gridingOn: Boolean = false
    private var isExportStarted: Boolean = false

    /**
     * Init new permission aksing
     */
    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            isGranted ->

                if (isGranted) {
                    enableLocationComponent(mMapboxMap.style!!)
                } else {
                    Toast.makeText(
                        context,
                        "R.string.user_location_permission_not_granted",
                        Toast.LENGTH_LONG
                    ).show()
                }
    }

    /**
     * Lazily initialize our [SearchDetailViewModel].
     */
    private val viewModel: SearchDetailViewModel by lazy {
        ViewModelProvider(this).get(SearchDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //transition effect
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sharedElementEnterTransition =
                TransitionInflater.from(context).inflateTransition(android.R.transition.move)

        }

        //MapBox init
        Mapbox.getInstance(requireContext(), getString(R.string.mapbox_access_token))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search_detail, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.about -> {
                showAboutAppDialog()
                return true
            }
            R.id.search -> {
                this.findNavController().navigate(SearchDetailFragmentDirections.actionShowSearch())
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showAboutAppDialog() {
        //Inflate the dialog with custom view
        val mDialogView = layoutInflater.inflate(R.layout.dialog_about_app, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(requireContext())
            .setView(mDialogView)
            .setTitle(getString(R.string.about_app))
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }
        //show dialog
        mBuilder.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val application = requireNotNull(activity).application
        val binding = FragmentSearchDetailBinding.inflate(inflater)
        binding.lifecycleOwner = this
        setHasOptionsMenu(true)

        //mapbox
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync { mapboxMap ->
            mMapboxMap = mapboxMap

            //listeners
            mapboxMap.addOnCameraMoveStartedListener(object :
                MapboxMap.OnCameraMoveStartedListener {

                private val REASONS = arrayOf(
                    "REASON_API_GESTURE",
                    "REASON_DEVELOPER_ANIMATION",
                    "REASON_API_ANIMATION"
                )

                override fun onCameraMoveStarted(reason: Int) {
                    //Toast.makeText(context, String.format("OnCameraMoveStarted: %s", REASONS[reason - 1]), Toast.LENGTH_SHORT/2).show()
                }
            })

            mapboxMap.addOnCameraMoveListener {
                //Toast.makeText(context, "onCameraMove", Toast.LENGTH_SHORT/2).show()
            }

            mapboxMap.addOnCameraMoveCancelListener {
                //Toast.makeText(context, "onCameraMoveCanceled", Toast.LENGTH_SHORT/2).show()
            }

            mapboxMap.addOnCameraIdleListener {
                if (gridingOn && !isExportStarted) {
                    isExportStarted = true;
                    requestForMapExportLayer()
                }
            }

            //show button for katastr grid
            grid_fab.show()

            //setting style
            mapboxMap.setStyle(Style.MAPBOX_STREETS) {
                // Map is set up and the style has loaded. Now you can add data or make other map adjustments
                //nazvy do cestiny
                val localizationPlugin = LocalizationPlugin(mapView!!, mapboxMap, it)
                try {
                    localizationPlugin.matchMapLanguageWithDeviceDefault()
                } catch (exception: RuntimeException) {
                }
                enableLocationComponent(it)
                arguments?.let { bundle ->
                    centerMapToSelectedProperty(bundle, mapboxMap, it)
                    viewModel.getDetails()
                }
            }
        }

        binding.gridFab.setOnClickListener {
            gridingOn = !gridingOn
            if (gridingOn) {
                binding.gridFab.setImageResource(R.drawable.ic_baseline_grid_off_24)
                requestForMapExportLayer()
            }
            else {
                binding.gridFab.setImageResource(R.drawable.ic_baseline_grid_on_24)
                removeMapExportLayer()
            }
        }


        val locationProperty =
            if (arguments != null)
                SearchDetailFragmentArgs.fromBundle(requireArguments()).selectedProperty
            else null

        val viewModelFactory = SearchDetailViewModelFactory(locationProperty, application)
        binding.viewModel = ViewModelProvider(
            this, viewModelFactory
        ).get(SearchDetailViewModel::class.java)

        //jestli se stahlo, zobraz to
        viewModel.propertiesIdentify.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                detail_info_nested_scroll_view.visibility = View.VISIBLE
            }
        })

        //jestli se nestahl stavebni objekt pro adresni misto, zobraz upozorneni
        viewModel.firstIdentify.observe(viewLifecycleOwner, Observer {
            if (viewModel.selectedProperty.value?.feature?.attributes?.typeTranslated == Type.ADRESNI_MISTO
                && it.layerId != 3
            ) { //stavebni objekt
                stavebni_objekt_not_found_layout.visibility = View.VISIBLE
            }
            expandInnerLayout(inflater, it.layerId)
        })

        //data stazena, jakmile se naplni prekonvertovany shape s parcelou(ami), zobraz polygon(y) v mape
        viewModel.polygonsCoordinates.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty())
                drawPolygonsToMap(it)
        })

        viewModel.propertyMapExport.observe(viewLifecycleOwner, Observer {

            val quad = it.extent?.toLatLngQuad()
            //get imageSource
            val imageSource = ImageSource(ID_IMAGE_SOURCE, quad, URI.create(it.href))
            // Create a raster layer and use the imageSource's ID as the layer's data. Then add a RasterLayer to the map.
            val rasterLayer = RasterLayer(ID_IMAGE_LAYER, ID_IMAGE_SOURCE)

            //remove old layer and source if exists
            removeMapExportLayer()

            //and add
            mMapboxMap.style?.addSource(imageSource)
            mMapboxMap.style?.addLayerBelow(rasterLayer, "building-number-label")

            isExportStarted = false

        })


        if (locationProperty != null) {

            binding.coordinateBottomSheet.katastrButton.setOnClickListener {
                val webpage: Uri =
                    Uri.parse("http://nahlizenidokn.cuzk.cz/MapaIdentifikace.aspx?l=KN&x=${locationProperty.feature.geometry.x.roundToInt()}&y=${locationProperty.feature.geometry.y.roundToInt()}")
                val intent = Intent(Intent.ACTION_VIEW, webpage)
                if (intent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivity(intent)
                }
            }

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
                val activities: List<ResolveInfo> =
                    requireActivity().packageManager.queryIntentActivities(mapIntent, 0)
                val isIntentSafe: Boolean = activities.isNotEmpty()

                // Show button if it's safe
                if (isIntentSafe) {
                    binding.coordinateBottomSheet.navigateButton.visibility = View.VISIBLE
                    binding.coordinateBottomSheet.navigateButton.setOnClickListener {
                        startActivity(
                            mapIntent
                        )
                    }
                }
            }
        }



        binding.coordinateBottomSheet.emptyDetailButtonExplanation.setOnClickListener {
            //Inflate the dialog with custom view
            val mDialogView =
                layoutInflater.inflate(R.layout.dialog_explanation_adrress_place, null)
            //AlertDialogBuilder
            val mBuilder = AlertDialog.Builder(requireContext())
                .setView(mDialogView)
                .setTitle(getString(R.string.title_explanation_dialog))
                .setPositiveButton(android.R.string.ok) { dialog, _ ->
                    dialog.dismiss()
                }
            //show dialog
            mBuilder.show()
        }

        if (locationProperty != null) {
            val behavior = BottomSheetBehavior.from(binding.coordinateBottomSheet.bottom_sheet_behaviour_layout)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        return binding.root
    }

    private fun centerMapToSelectedProperty(
        bundle: Bundle,
        mapboxMap: MapboxMap,
        style: Style
    ) {
        val locationProperty =
            SearchDetailFragmentArgs.fromBundle(bundle).selectedProperty

        val northeast = LatLng(
            locationProperty.feature.attributes.ymin,
            locationProperty.feature.attributes.xmin
        )

        val southwest = LatLng(
            locationProperty.feature.attributes.ymax,
            locationProperty.feature.attributes.xmax
        )

        val latLngBounds: LatLngBounds = LatLngBounds.Builder()
            .include(northeast) // Northeast
            .include(southwest) // Southwest
            .build()

        if (locationProperty.feature.attributes.isPoint) {
            mapboxMap.easeCamera(
                CameraUpdateFactory.newLatLngZoom(latLngBounds.center, 18.0),
                2500
            )

            //make marker
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_place_brown_24dp)?.let {
                style.addImage(
                    "icon-id",
                    it
                )
            }

            style.addSource(
                GeoJsonSource
                    (
                    "source-id", Feature.fromGeometry(
                    Point.fromLngLat(
                        latLngBounds.center.longitude,
                        latLngBounds.center.latitude
                    )
                )
                )
            )

            style.addLayer(
                SymbolLayer(
                    "layer-id",
                    "source-id"
                ).withProperties(
                    iconImage("icon-id"),
                    iconAllowOverlap(true),
                    iconIgnorePlacement(true)
                )
            )
        } else {
            mapboxMap.easeCamera(
                CameraUpdateFactory.newLatLngBounds(latLngBounds, 0),
                2500
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(context)) {

            // Create and customize the LocationComponent's options
            val customLocationComponentOptions = LocationComponentOptions.builder(requireContext())
                .trackingGesturesManagement(true)
                .build()

            val locationComponentActivationOptions =
                LocationComponentActivationOptions.builder(requireContext(), loadedMapStyle)
                    .locationComponentOptions(customLocationComponentOptions)
                    .build()

            // Get an instance of the LocationComponent and then adjust its settings
            mMapboxMap.locationComponent.apply {

                // Activate the LocationComponent with options
                activateLocationComponent(locationComponentActivationOptions)

                // Enable to make the LocationComponent visible
                isLocationComponentEnabled = true

                // Set the LocationComponent's camera mode
                cameraMode = CameraMode.TRACKING

                // Set the LocationComponent's render mode
                renderMode = RenderMode.COMPASS
            }
        } else {
            requestPermission.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
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
        Type.ZAKLADNI_SIDELNI_JEDNOTKA.layerId -> R.layout.detail_zakladni_sidelni_jednotka
        Type.KATASTRALNI_UZEMI.layerId -> R.layout.detail_katastralni_uzemi
        Type.MESTSKY_OBVOD_MESTSKA_CAST.layerId -> R.layout.detail_mestsky_obvod_mestska_cast
        Type.SPRAVNI_OBVOD_PRAHA.layerId -> R.layout.detail_spravni_obvod_praha
        Type.CAST_OBCE.layerId -> R.layout.detail_cast_obce
        Type.OBEC.layerId -> R.layout.detail_obec
        Type.OBEC_SPOU.layerId -> R.layout.detail_obec_spou
        Type.OBEC_SROP.layerId -> R.layout.detail_obec_srop
        Type.OKRES.layerId -> R.layout.detail_okres
        Type.VYSSI_CELEK.layerId -> R.layout.detail_vyssi_celek
        else -> R.layout.detail_stavebni_objekt
    }

    private fun drawPolygonsToMap(arrayList: ArrayList<ArrayList<DoubleArray>>) {
        val features = arrayListOf<Feature>()

        for (array in arrayList) {
            val polygonCoordinates = ArrayList<Point>()
            for (point in array)
                polygonCoordinates.add(Point.fromLngLat(point[1], point[0]))

            features.add(Feature.fromGeometry(LineString.fromLngLats(polygonCoordinates)))
        }

        // Create the LineString from the list of coordinates and then make a GeoJSON
        // FeatureCollection so we can add the line to our map as a layer.
        mMapboxMap.style?.addSource(
            GeoJsonSource(
                "line-source",
                FeatureCollection.fromFeatures(
                    features
                )
            )
        )

        // The layer properties for our line. This is where we make the line dotted, set the
        // color, etc.
        mMapboxMap.style?.addLayer(
            LineLayer("linelayer", "line-source").withProperties(
                PropertyFactory.lineWidth(2f),
                PropertyFactory.lineColor(Color.parseColor("#e55e5e"))
            )
        )
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView?.onDestroy()
    }

    private fun Extent.toLatLngQuad(): LatLngQuad {

        return LatLngQuad(
            LatLng(CoordinatesConvertor.y2lat(this.ymax), CoordinatesConvertor.x2lon(this.xmin)),
            LatLng(CoordinatesConvertor.y2lat(this.ymax), CoordinatesConvertor.x2lon(this.xmax)),
            LatLng(CoordinatesConvertor.y2lat(this.ymin), CoordinatesConvertor.x2lon(this.xmax)),
            LatLng(CoordinatesConvertor.y2lat(this.ymin), CoordinatesConvertor.x2lon(this.xmin))
        )
    }

    private fun removeMapExportLayer() {
        //remove old layer and source if exists
        if (mMapboxMap.style?.layers?.any { it -> it.id == ID_IMAGE_LAYER }!!) {
            mMapboxMap.style?.removeLayer(ID_IMAGE_LAYER)
            mMapboxMap.style?.removeSource(ID_IMAGE_SOURCE)
        }
    }

    private fun requestForMapExportLayer() {
        val latLngBoundsZoom =
            mMapboxMap.getLatLngBoundsZoomFromCamera(mMapboxMap.cameraPosition)

        val northEast = latLngBoundsZoom.latLngBounds.northEast
        val southWest = latLngBoundsZoom.latLngBounds.southWest

        //getDPI
        val metrics: DisplayMetrics = resources.displayMetrics
        val dpi = metrics.densityDpi

        viewModel.getMapImage(
            southWest,
            northEast,
            mMapboxMap.height,
            mMapboxMap.width,
            dpi
        )
        //a cekame v observeru na vysledek
    }


}
