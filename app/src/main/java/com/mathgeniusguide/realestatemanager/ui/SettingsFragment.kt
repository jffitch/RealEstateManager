package com.mathgeniusguide.realestatemanager.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mathgeniusguide.realestatemanager.MainActivity
import com.mathgeniusguide.realestatemanager.R
import com.mathgeniusguide.realestatemanager.utils.Constants
import com.mathgeniusguide.realestatemanager.utils.Functions.hide
import com.mathgeniusguide.realestatemanager.utils.Functions.show
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.settings_fragment.*

class SettingsFragment : Fragment() {
    lateinit var act: MainActivity
    private var pref: SharedPreferences? = null
    // set variables to defaults in case values are not loaded
    private var locationMethod = Constants.LOCATION_ACTUAL
    var latitude = Constants.LATITUDE_DEFAULT
    var longitude = Constants.LONGITUDE_DEFAULT
    var address = Constants.ADDRESS_DEFAULT
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.settings_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        act = activity as MainActivity
        act.toolbar.visibility = View.VISIBLE
        act.toolbar.navigationIcon = null

        // load values from Shared Preferences and use values to set text for views
        pref = context?.getSharedPreferences(Constants.PREF, 0)
        locationMethod = pref?.getInt(Constants.STRING_LOCATION_METHOD, Constants.LOCATION_ACTUAL) ?: Constants.LOCATION_ACTUAL
        latitude = pref?.getFloat(Constants.STRING_LATITUDE, Constants.LATITUDE_DEFAULT.toFloat())?.toDouble() ?: Constants.LATITUDE_DEFAULT
        longitude = pref?.getFloat(Constants.STRING_LONGITUDE, Constants.LONGITUDE_DEFAULT.toFloat())?.toDouble() ?: Constants.LONGITUDE_DEFAULT
        address = pref?.getString(Constants.STRING_ADDRESS, Constants.ADDRESS_DEFAULT) ?: Constants.ADDRESS_DEFAULT

        latitudeField.setText(latitude.toString())
        longitudeField.setText(longitude.toString())
        addressField.setText(address)
        updateChecked(locationMethod)
        setUpButton()
        setUpRG()
    }

    private fun updateChecked(locationMethod: Int) {
        // check the Radio Button based on locationMethod from Shared Preferences
        val view = when (locationMethod) {
            Constants.LOCATION_COORDINATES -> R.id.locationCoordinates
            Constants.LOCATION_ADDRESS -> R.id.locationAddress
            else -> R.id.locationActual
        }
        locationRG.check(view)
        updateView(view)
    }

    private fun updateView(view: Int) {
        // update view based on how location is calculated
        when (view) {
            R.id.locationActual -> {
                // only RadioGroup is visible
                hide(latitudeLabel, latitudeField, longitudeLabel, longitudeField, addressLabel, addressField)
            }
            R.id.locationCoordinates -> {
                // latitude and longitude fields are visible
                show(latitudeLabel, latitudeField, longitudeLabel, longitudeField)
                hide(addressLabel, addressField)
            }
            R.id.locationAddress -> {
                // address field is visible
                show(addressLabel, addressField)
                hide(latitudeLabel, latitudeField, longitudeLabel, longitudeField)
            }
        }
    }

    private fun setUpButton() {
        saveButton.setOnClickListener {
            // when button is clicked, get values from fields and save to Shared Preferences
            if (latitudeField.text.isNotEmpty()) {
                latitude = latitudeField.text.toString().toDouble()
            }
            if (longitudeField.text.isNotEmpty()) {
                longitude = longitudeField.text.toString().toDouble()
            }
            if (addressField.text.isNotEmpty()) {
                address = addressField.text.toString()
            }
            locationMethod = when(locationRG.checkedRadioButtonId) {
                R.id.locationCoordinates -> Constants.LOCATION_COORDINATES
                R.id.locationAddress -> Constants.LOCATION_ADDRESS
                else -> Constants.LOCATION_ACTUAL
            }
            val editor = pref?.edit()
            editor?.putInt(Constants.STRING_LOCATION_METHOD, locationMethod)
            editor?.putFloat(Constants.STRING_LATITUDE, latitude.toFloat())
            editor?.putFloat(Constants.STRING_LONGITUDE, longitude.toFloat())
            editor?.putString(Constants.STRING_ADDRESS, address)
            editor?.apply()
        }
    }

    private fun setUpRG() {
        locationRG.setOnCheckedChangeListener { _, i ->
            // update view based on which item in Radio Group is clicked
            updateView(i)
        }
    }
}