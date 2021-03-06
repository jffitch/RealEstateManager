package com.mathgeniusguide.realestatemanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.mathgeniusguide.realestatemanager.MainActivity
import com.mathgeniusguide.realestatemanager.R
import com.mathgeniusguide.realestatemanager.database.HouseFirebaseItem
import com.mathgeniusguide.realestatemanager.objects.MediaImage
import com.mathgeniusguide.realestatemanager.utils.*
import com.mathgeniusguide.realestatemanager.utils.Functions.fullLocation
import com.mathgeniusguide.realestatemanager.utils.Functions.hide
import com.mathgeniusguide.realestatemanager.viewModel.HousesViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_house_fragment.*
import java.text.SimpleDateFormat
import java.util.*

class AddHouseFragment : Fragment() {
    private val viewModel by lazy { ViewModelProviders.of(activity as MainActivity).get(HousesViewModel::class.java) }
    private val imageList = emptyList<MediaImage>().toMutableList()
    lateinit var act: MainActivity

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
        // hide fields related to sale, as they are not used when adding a new house
        hide(saleLabel, saleDateLabel, saleDateField, soldLabel, soldField)
        setUpButtons()
    }

    private fun setUpButtons() {
        addHouseButton.setOnClickListener {
            if (arrayOf(areaField, bathField, bedField, boroughField, addressField, cityField, zipField, priceField, roomsField).all { it.text.isNotEmpty() }) {
                // if required fields are filled, get information from fields and store in HouseFirebaseItem object
                // listDate is today's Date
                // saleDate is empty
                // fetch latitude and longitude using ViewModel function
                val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                val house = HouseFirebaseItem()
                house.agent = act.username
                house.area = areaField.text.toString().toInt()
                house.bathrooms = bathField.text.toString().toInt()
                house.bedrooms = bedField.text.toString().toInt()
                house.borough = boroughField.text.toString()
                house.description = descriptionField.text.toString()
                house.images = imageList.toFirebaseList()
                house.listDate = sdf.format(Date())
                house.location = fullLocation(addressField.text.toString(), cityField.text.toString(), zipField.text.toString())
                val price = priceField.text.toString().toInt()
                house.price = if (Locale.getDefault().language != "en") price.convertEurosToDollars() else price
                house.rooms = roomsField.text.toString().toInt()
                house.saleDate = ""
                house.type = when (typeField.checkedRadioButtonId) {
                    R.id.house -> Constants.HOUSE
                    R.id.flat -> Constants.FLAT
                    R.id.duplex -> Constants.DUPLEX
                    R.id.penthouse -> Constants.PENTHOUSE
                    else -> 0
                }
                if (context?.isOnline() ?: false) {
                    // if internet connection, fetch coordinates from typed address
                    viewModel.fetchHouseCoordinates(house, true, context!!)
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
    }
}

