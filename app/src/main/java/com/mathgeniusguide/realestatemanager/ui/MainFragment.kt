package com.mathgeniusguide.realestatemanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.mathgeniusguide.realestatemanager.MainActivity
import com.mathgeniusguide.realestatemanager.R
import com.mathgeniusguide.realestatemanager.adapter.HouseAdapter
import com.mathgeniusguide.realestatemanager.adapter.MediaAdapter
import com.mathgeniusguide.realestatemanager.database.HouseFirebaseItem
import com.mathgeniusguide.realestatemanager.objects.MediaImage
import com.mathgeniusguide.realestatemanager.utils.toAPI
import com.mathgeniusguide.realestatemanager.utils.toMediaImage
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.android.synthetic.main.main_fragment_details.*

class MainFragment: Fragment() {
    lateinit var act: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        act = activity as MainActivity
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.main_fragment, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        houseList.layoutManager = LinearLayoutManager(context)
        act.title = String.format(resources.getString(R.string.welcome), act.username)

        if (act.filteredHouseItemList.isEmpty()) {
            if (act.firebaseLoaded.value!! || act.roomdbLoaded.value!!) {
                houseList.adapter = HouseAdapter(act.houseItemList as ArrayList<HouseFirebaseItem>, context!!, act, surfaceStats, roomsStats, bathroomsStats, bedroomsStats, locationStats, descriptionText, imageList, googleMap)
                clickFirst()
            }

            act.firebaseLoaded.observe(viewLifecycleOwner, Observer {
                if (it != null && it) {
                    houseList.adapter = HouseAdapter(act.houseItemList as ArrayList<HouseFirebaseItem>, context!!, act, surfaceStats, roomsStats, bathroomsStats, bedroomsStats, locationStats, descriptionText, imageList, googleMap)
                    clickFirst()
                }
            })

            act.roomdbLoaded.observe(viewLifecycleOwner, Observer {
                if (it != null && it) {
                    houseList.adapter = HouseAdapter(act.houseItemList as ArrayList<HouseFirebaseItem>, context!!, act, surfaceStats, roomsStats, bathroomsStats, bedroomsStats, locationStats, descriptionText, imageList, googleMap)
                    clickFirst()
                }
            })
        } else {
            houseList.adapter = HouseAdapter(act.filteredHouseItemList as ArrayList<HouseFirebaseItem>, context!!, act, surfaceStats, roomsStats, bathroomsStats, bedroomsStats, locationStats, descriptionText, imageList, googleMap)
            clickFirst()
        }
    }

    fun clickFirst() {
        val list = act.houseItemList
        if (list.isNotEmpty()) {
            val i = list[0]
            val images: List<MediaImage> = if (i.images != null) i.images!!.split("||").map {it.toMediaImage ()} else emptyList()
            act.houseSelected = i.id ?: ""
            surfaceStats.text = String.format(context!!.resources.getString(R.string.number_sq_m), i.area)
            roomsStats.text = i.rooms.toString()
            bathroomsStats.text = i.bathrooms.toString()
            bedroomsStats.text = i.bedrooms.toString()
            locationStats.text = Regex("\\|").replace(i.location!!, "\n")
            descriptionText.text = i.description
            imageList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            imageList.adapter = MediaAdapter(images as ArrayList<MediaImage>, context!!)
            Glide.with(context!!).load(i.location!!.toAPI(250,250,15)).into(googleMap)
        }
    }
}