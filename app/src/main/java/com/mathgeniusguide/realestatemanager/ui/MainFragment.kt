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
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.android.synthetic.main.main_fragment_details.*

class MainFragment : Fragment() {
    lateinit var act: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        act = activity as MainActivity
        act.toolbar.visibility = View.VISIBLE
        act.toolbar.navigationIcon = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        houseList.layoutManager = LinearLayoutManager(context)
        act.title = String.format(resources.getString(R.string.welcome), act.username)

        // use filteredHouseItemList to determine if loaded from Search
        // filteredHouseItemList will not be empty if loaded from Search
        // RecyclerView will be populated using filteredHouseItemList if not empty, otherwise houseItemList
        if (act.filteredHouseItemList.isEmpty()) {
            // if data was already loaded from Firebase or Room Database, populate RecyclerView with houseItemList
            if (act.firebaseLoaded.value ?: false || act.roomdbLoaded.value ?: false) {
                houseList.adapter = HouseAdapter(act.houseItemList as ArrayList<HouseFirebaseItem>, context!!, act, surfaceStats, roomsStats, bathroomsStats, bedroomsStats, locationStats, descriptionText, imageList, googleMap)
                clickFirst(act.houseItemList)
            }

            // if data was not already loaded from Firebase or Room Database, observe both and populate RecyclerView with houseItemList when loaded
            act.firebaseLoaded.observe(viewLifecycleOwner, Observer {
                if (it != null && it) {
                    houseList.adapter = HouseAdapter(act.houseItemList as ArrayList<HouseFirebaseItem>, context!!, act, surfaceStats, roomsStats, bathroomsStats, bedroomsStats, locationStats, descriptionText, imageList, googleMap)
                    clickFirst(act.houseItemList)
                }
            })

            act.roomdbLoaded.observe(viewLifecycleOwner, Observer {
                if (it != null && it) {
                    houseList.adapter = HouseAdapter(act.houseItemList as ArrayList<HouseFirebaseItem>, context!!, act, surfaceStats, roomsStats, bathroomsStats, bedroomsStats, locationStats, descriptionText, imageList, googleMap)
                    clickFirst(act.houseItemList)
                }
            })
        } else {
            // if filteredHouseItemList is not empty, a Search was performed
            // populate RecyclerView with filteredHouseItemList
            houseList.adapter = HouseAdapter(act.filteredHouseItemList as ArrayList<HouseFirebaseItem>, context!!, act, surfaceStats, roomsStats, bathroomsStats, bedroomsStats, locationStats, descriptionText, imageList, googleMap)
            clickFirst(act.filteredHouseItemList)
        }
    }

    fun clickFirst(list: List<HouseFirebaseItem>) {
        val house: HouseFirebaseItem?
        if (act.houseSelected == "") {
            if (list.isEmpty()) {
                return
            }
            house = list[0]
        } else {
            if (act.houseItemList.none { it.id == act.houseSelected }) {
                return
            }
            house = act.houseItemList.first { it.id == act.houseSelected }
        }
        val images: List<MediaImage> = if (house.images != null) house.images!!.split("||").map { it.toMediaImage() } else emptyList()
        act.houseSelected = house.id ?: ""
        surfaceStats.text = String.format(context!!.resources.getString(R.string.number_sq_m), house.area)
        roomsStats.text = house.rooms.toString()
        bathroomsStats.text = house.bathrooms.toString()
        bedroomsStats.text = house.bedrooms.toString()
        locationStats.text = Regex("\\|").replace(house.location!!, "\n")
        descriptionText.text = house.description
        imageList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        imageList.adapter = MediaAdapter(images as ArrayList<MediaImage>, context!!)
        Glide.with(context!!).load(house.location!!.toAPI(250, 250, 15)).into(googleMap)
    }
}