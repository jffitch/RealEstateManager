package com.mathgeniusguide.realestatemanager.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.mathgeniusguide.realestatemanager.MainActivity
import com.mathgeniusguide.realestatemanager.R
import com.mathgeniusguide.realestatemanager.database.HouseFirebaseItem
import com.mathgeniusguide.realestatemanager.objects.MediaImage
import com.mathgeniusguide.realestatemanager.utils.*
import com.mathgeniusguide.realestatemanager.utils.Functions.hide
import com.mathgeniusguide.realestatemanager.utils.Functions.show
import com.mathgeniusguide.realestatemanager.viewModel.HousesViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_house_fragment.*
import java.text.SimpleDateFormat
import java.util.*

class EditHouseFragment: Fragment() {
    private val viewModel by lazy { ViewModelProviders.of(activity as MainActivity).get(HousesViewModel::class.java) }
    private var imageList = emptyList<MediaImage>().toMutableList()
    lateinit var act: MainActivity
    private lateinit var houseItem: HouseFirebaseItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.add_house_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        act = activity as MainActivity
        act.toolbar.visibility = View.VISIBLE
        act.toolbar.navigationIcon = null
        loadInfo()
        setUpButtons()
    }

    private fun loadInfo() {
        // load info into fields
        val houseID = act.houseSelected
        if (act.houseItemList.none { it.id == houseID }) {
            // prevent crash if house with this ID doesn't exist
            return
        }
        houseItem = act.houseItemList.first { it.id == houseID }
        imageList = (houseItem.images ?: "").toImageList()
        areaField.setText(houseItem.area.toString())
        bathField.setText(houseItem.bathrooms.toString())
        bedField.setText(houseItem.bedrooms.toString())
        boroughField.setText(houseItem.borough.toString())
        descriptionField.setText(houseItem.description.toString())
        imagesList.text = imageList.toTextList()
        val price = if (Locale.getDefault().language != "en") (houseItem.price ?: 0).convertDollarsToEuros() else houseItem.price ?: 0
        priceField.setText(price.toString())
        roomsField.setText(houseItem.rooms.toString())
        typeField.check(when (houseItem.type) {
            Constants.FLAT -> R.id.flat
            Constants.DUPLEX -> R.id.duplex
            Constants.PENTHOUSE -> R.id.penthouse
            else -> R.id.house
        })
        soldField.isChecked = (!houseItem.saleDate.isNullOrEmpty())
        if (soldField.isChecked) {
            show(saleDateLabel, saleDateField)
        } else {
            hide(saleDateLabel, saleDateField)
        }
        saleDateField.setText(houseItem.saleDate)
        // Firebase stores location as one field
        // split into address, city, and zip fields
        val location = houseItem.location.splitLocation()
        addressField.setText(location.address)
        cityField.setText(location.city)
        zipField.setText(location.zip)
    }

    private fun setUpButtons() {
        addHouseButton.text = resources.getString(R.string.update_house)
        addHouseButton.setOnClickListener {
            Log.d("Real Estate Manager", "Update House Button Clicked")
            if (arrayOf(areaField, bathField, bedField, boroughField, addressField, cityField, zipField, priceField, roomsField).all { it.text.isNotEmpty() }) {
                // if required fields are filled, get information from fields and store in HouseFirebaseItem object
                // listDate is unchanged from loaded listDate
                // saleDate is empty
                // fetch latitude and longitude using ViewModel function
                Log.d("Real Estate Manager", "Update House Button Code Run")
                val house = HouseFirebaseItem()
                house.id = houseItem.id
                house.agent = act.username
                house.area = areaField.text.toString().toInt()
                house.bathrooms = bathField.text.toString().toInt()
                house.bedrooms = bedField.text.toString().toInt()
                house.borough = boroughField.text.toString()
                house.description = descriptionField.text.toString()
                house.images = imageList.toFirebaseList()
                house.listDate = houseItem.listDate
                house.location = Functions.fullLocation(addressField.text.toString(), cityField.text.toString(), zipField.text.toString())
                val price = priceField.text.toString().toInt()
                house.price = if (Locale.getDefault().language != "en") price.convertEurosToDollars() else price
                house.rooms = roomsField.text.toString().toInt()
                house.saleDate = if (soldField.isChecked) saleDateField.text.toString().replace(Regex("\\D"), "/") else ""
                house.type = when (typeField.checkedRadioButtonId) {
                    R.id.house -> Constants.HOUSE
                    R.id.flat -> Constants.FLAT
                    R.id.duplex -> Constants.DUPLEX
                    R.id.penthouse -> Constants.PENTHOUSE
                    else -> 0
                }
                if (context?.isOnline() ?: false) {
                    // if internet connection, fetch coordinates from typed address
                    viewModel.fetchHouseCoordinates(house, false, context!!)
                } else {
                    // if no internet connection, show error message
                    Toast.makeText(context, resources.getString(R.string.no_internet), Toast.LENGTH_LONG).show()
                }
            } else {
                // if required fields are not filled, show error message
                Toast.makeText(context, resources.getString(R.string.fields_empty), Toast.LENGTH_LONG).show()
            }
        }
        addImageButton.setOnClickListener {
            // when Add Image is clicked, if image room and URL are filled, add image to list and update TextView
            if (imageRoomField.text.isNotEmpty() && imageUrlField.text.isNotEmpty()) {
                val image = MediaImage()
                image.room = imageRoomField.text.toString()
                image.url = imageUrlField.text.toString()
                imageList.add(image)
                imagesList.text = imageList.toTextList()
            }
        }
        removeImageButton.setOnClickListener {
            // when Remove Image is clicked, remove most recent image from list and update TextView
            if (imageList.isNotEmpty()) {
                imageList.removeAt(imageList.lastIndex)
                imagesList.text = imageList.toTextList()
            }
        }
        soldField.setOnClickListener {
            if ((it as CheckBox).isChecked) {
                show(saleDateLabel, saleDateField)
                val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                saleDateField.setText(sdf.format(Date()))
            } else {
                hide(saleDateLabel, saleDateField)
            }
        }
    }
}