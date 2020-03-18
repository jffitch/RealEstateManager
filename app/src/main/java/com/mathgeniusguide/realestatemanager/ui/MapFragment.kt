package com.mathgeniusguide.realestatemanager.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.mathgeniusguide.realestatemanager.MainActivity
import com.mathgeniusguide.realestatemanager.R
import com.mathgeniusguide.realestatemanager.database.HouseFirebaseItem
import com.mathgeniusguide.realestatemanager.utils.Constants
import com.mathgeniusguide.realestatemanager.utils.toHouseType
import kotlinx.android.synthetic.main.map_fragment.*
import java.util.*

class MapFragment: Fragment(), OnMapReadyCallback {
    var googleMap: GoogleMap? = null
    // set latitude and longitude to impossible values as placeholders
    var latitude = 91.0
    var longitude = 181.0
    lateinit var act: MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.map_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // declare activity shorthand variable
        act = activity as MainActivity
        // toolbar visible and back arrow as drawer icon
        map?.onCreate(null)
        map?.onResume()
        map?.getMapAsync(this)
    }

    // create markers for Google map
    fun getMarkers(list: List<HouseFirebaseItem>) {
        var coord: LatLng? = null
        var title = ""
        var pos: CameraPosition? = null
        var marker: Marker?
        for (house in list) {
            coord = LatLng(house.latitude ?: 0.0, house.longitude ?: 0.0)
            title = "${(house.type ?: Constants.HOUSE).toHouseType(resources)}: ${String.format(resources.getString(R.string.dollar_sign), "%,d".format(house.price))}"
            marker = googleMap?.addMarker(MarkerOptions().position(coord).title(title))
            act.markerList.add(marker)
        }
    }

    // run when Google map is ready
    override fun onMapReady(p0: GoogleMap?) {
        MapsInitializer.initialize(context)
        googleMap = p0
        getMarkers(act.houseItemList)
        setLocation(40.7128, 74.0060)
        googleMap?.setOnInfoWindowClickListener {marker->
            // marker clicked
        }
    }

    // if location sensor has retrieved both latitude and longitude values, go to location on map
    fun setLocation(lat: Double, lng: Double) {
        if (lat != 91.0 && lng != 181.0) {
            val coord = LatLng(lat, lng)
            googleMap?.apply {
                this.mapType = GoogleMap.MAP_TYPE_NORMAL
                this.addMarker(MarkerOptions().position(coord).title(resources.getString(R.string.you_are_here)))
                val pos =
                        CameraPosition.builder().target(coord).zoom(16.toFloat()).bearing(0.toFloat())
                                .tilt(45.toFloat()).build()
                this.moveCamera(CameraUpdateFactory.newCameraPosition(pos))
            }
        }
    }
}