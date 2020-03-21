package com.mathgeniusguide.realestatemanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mathgeniusguide.realestatemanager.MainActivity
import com.mathgeniusguide.realestatemanager.R
import com.mathgeniusguide.realestatemanager.utils.Functions.filled
import com.mathgeniusguide.realestatemanager.utils.toHouseTypeConstant
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.search_houses_fragment.*

class SearchHousesFragment: Fragment() {
    lateinit var act: MainActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.search_houses_fragment, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        act = activity as MainActivity
        act.toolbar.visibility = View.VISIBLE
        act.toolbar.navigationIcon = null
        searchButton.setOnClickListener {
            searchButtonClicked()
        }
    }
    fun searchButtonClicked() {
        // when search button is clicked, set filteredHouseItemList to houseItemList
        // filter by each filled field, empty fields are ignored
        act.filteredHouseItemList = act.houseItemList
        if (filled(priceMinField)) {
            act.filteredHouseItemList = act.filteredHouseItemList.filter {it.price != null && it.price!! >= priceMinField.text.toString().toInt()}.toMutableList()
        }
        if (filled(priceMaxField)) {
            act.filteredHouseItemList = act.filteredHouseItemList.filter {it.price != null && it.price!! <= priceMaxField.text.toString().toInt()}.toMutableList()
        }
        if (typeRG.checkedRadioButtonId != R.id.anyType) {
            act.filteredHouseItemList = act.filteredHouseItemList.filter {it.type != null && it.type!! == typeRG.checkedRadioButtonId.toHouseTypeConstant()}.toMutableList()
        }
        if (filled(areaMinField)) {
            act.filteredHouseItemList = act.filteredHouseItemList.filter {it.area != null && it.area!! >= areaMinField.text.toString().toInt()}.toMutableList()
        }
        if (filled(areaMaxField)) {
            act.filteredHouseItemList = act.filteredHouseItemList.filter {it.area != null && it.area!! <= areaMaxField.text.toString().toInt()}.toMutableList()
        }
        if (filled(roomsMinField)) {
            act.filteredHouseItemList = act.filteredHouseItemList.filter {it.rooms != null && it.rooms!! >= roomsMinField.text.toString().toInt()}.toMutableList()
        }
        if (filled(roomsMaxField)) {
            act.filteredHouseItemList = act.filteredHouseItemList.filter {it.rooms != null && it.rooms!! <= roomsMaxField.text.toString().toInt()}.toMutableList()
        }
        if (filled(bathroomsMinField)) {
            act.filteredHouseItemList = act.filteredHouseItemList.filter {it.bathrooms != null && it.bathrooms!! >= bathroomsMinField.text.toString().toInt()}.toMutableList()
        }
        if (filled(bathroomsMaxField)) {
            act.filteredHouseItemList = act.filteredHouseItemList.filter {it.bathrooms != null && it.bathrooms!! <= bathroomsMaxField.text.toString().toInt()}.toMutableList()
        }
        if (filled(bedroomsMinField)) {
            act.filteredHouseItemList = act.filteredHouseItemList.filter {it.bedrooms != null && it.bedrooms!! >= bedroomsMinField.text.toString().toInt()}.toMutableList()
        }
        if (filled(bedroomsMaxField)) {
            act.filteredHouseItemList = act.filteredHouseItemList.filter {it.bedrooms != null && it.bedrooms!! <= bedroomsMaxField.text.toString().toInt()}.toMutableList()
        }
        if (filled(boroughField)) {
            act.filteredHouseItemList = act.filteredHouseItemList.filter {it.borough != null && it.borough!!.contains(boroughField.text.toString())}.toMutableList()
        }
        if (filled(locationField)) {
            act.filteredHouseItemList = act.filteredHouseItemList.filter {it.location != null && it.location!!.contains(locationField.text.toString())}.toMutableList()
        }
        if (filled(agentField)) {
            act.filteredHouseItemList = act.filteredHouseItemList.filter {it.agent != null && it.agent!!.contains(agentField.text.toString())}.toMutableList()
        }
        if (filled(listDateMinField)) {
            act.filteredHouseItemList = act.filteredHouseItemList.filter {it.listDate != null && it.listDate!! >= listDateMinField.text.toString().replace(Regex("\\D"), "/")}.toMutableList()
        }
        if (filled(listDateMaxField)) {
            act.filteredHouseItemList = act.filteredHouseItemList.filter {it.listDate != null && it.listDate!! <= listDateMaxField.text.toString().replace(Regex("\\D"), "/")}.toMutableList()
        }
        if (soldRG.checkedRadioButtonId != R.id.anySold) {
            act.filteredHouseItemList = act.filteredHouseItemList.filter {it.saleDate.isNullOrEmpty() == (soldRG.checkedRadioButtonId == R.id.falseSold)}.toMutableList()
        }
        if (filled(saleDateMinField)) {
            act.filteredHouseItemList = act.filteredHouseItemList.filter {it.saleDate != null && it.saleDate!! >= saleDateMinField.text.toString().replace(Regex("\\D"), "/")}.toMutableList()
        }
        if (filled(saleDateMaxField)) {
            act.filteredHouseItemList = act.filteredHouseItemList.filter {it.saleDate != null && it.saleDate!! <= saleDateMaxField.text.toString().replace(Regex("\\D"), "/")}.toMutableList()
        }
        // if no houses match search criteria, show error message
        // otherwise, go to Main fragment
        // filteredHouseItemList will be loaded in Main fragment
        if (act.filteredHouseItemList.isEmpty()) {
            Toast.makeText(context, resources.getString(R.string.no_search_results), Toast.LENGTH_LONG).show()
        } else {
            act.houseSelected = ""
            findNavController().navigate(R.id.action_search_to_main)
        }
    }
}