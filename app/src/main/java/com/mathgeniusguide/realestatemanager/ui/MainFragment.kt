package com.mathgeniusguide.realestatemanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mathgeniusguide.realestatemanager.MainActivity
import com.mathgeniusguide.realestatemanager.R
import com.mathgeniusguide.realestatemanager.adapter.HouseAdapter
import com.mathgeniusguide.realestatemanager.database.HouseFirebaseItem
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
                houseList.adapter = HouseAdapter(act.houseItemList as ArrayList<HouseFirebaseItem>, context!!, surfaceStats, roomsStats, bathroomsStats, bedroomsStats, locationStats, descriptionText, imageList, googleMap)
            }

            act.firebaseLoaded.observe(viewLifecycleOwner, Observer {
                if (it != null && it) {
                    houseList.adapter = HouseAdapter(act.houseItemList as ArrayList<HouseFirebaseItem>, context!!, surfaceStats, roomsStats, bathroomsStats, bedroomsStats, locationStats, descriptionText, imageList, googleMap)
                }
            })

            act.roomdbLoaded.observe(viewLifecycleOwner, Observer {
                if (it != null && it) {
                    houseList.adapter = HouseAdapter(act.houseItemList as ArrayList<HouseFirebaseItem>, context!!, surfaceStats, roomsStats, bathroomsStats, bedroomsStats, locationStats, descriptionText, imageList, googleMap)
                }
            })
        } else {
            houseList.adapter = HouseAdapter(act.filteredHouseItemList as ArrayList<HouseFirebaseItem>, context!!, surfaceStats, roomsStats, bathroomsStats, bedroomsStats, locationStats, descriptionText, imageList, googleMap)
        }
    }
}