package com.mathgeniusguide.realestatemanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mathgeniusguide.realestatemanager.MainActivity
import com.mathgeniusguide.realestatemanager.R
import com.mathgeniusguide.realestatemanager.database.HouseFirebaseItem
import com.mathgeniusguide.realestatemanager.objects.MediaImage
import com.mathgeniusguide.realestatemanager.utils.*
import kotlinx.android.synthetic.main.add_fragment.*

class EditHouseFragment: Fragment() {
    var imageList = emptyList<MediaImage>().toMutableList()
    lateinit var act: MainActivity
    lateinit var houseItem: HouseFirebaseItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.add_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        act = activity as MainActivity
        loadInfo()
        setUpButtons()
    }

    fun loadInfo() {
        val houseID = act.houseSelected
        if (act.houseItemList.none { it.id == houseID }) {
            return
        }
        houseItem = act.houseItemList.first { it.id == houseID }
        val location = houseItem.location.splitLocation()
        imageList = (houseItem.images ?: "").toImageList()
        areaField.setText(houseItem.area.toString())
        bathField.setText(houseItem.bathrooms.toString())
        bedField.setText(houseItem.bedrooms.toString())
        boroughField.setText(houseItem.borough.toString())
        descriptionField.setText(houseItem.description.toString())
        imagesList.text = imageList.toTextList()
        priceField.setText(houseItem.price.toString())
        roomsField.setText(houseItem.rooms.toString())
        typeField.check(when (houseItem.type) {
            Constants.FLAT -> R.id.flat
            Constants.DUPLEX -> R.id.duplex
            Constants.PENTHOUSE -> R.id.penthouse
            else -> R.id.house
        })
        addressField.setText(location.address)
        cityField.setText(location.city)
        zipField.setText(location.zip)
    }

    fun setUpButtons() {
        addHouseButton.text = resources.getString(R.string.update_house)
        addHouseButton.setOnClickListener {
            if (arrayOf(areaField, bathField, bedField, boroughField, addressField, cityField, zipField, priceField, roomsField).all { it.text.isNotEmpty() }) {
                val type = when (typeField.checkedRadioButtonId) {
                    R.id.house -> Constants.HOUSE
                    R.id.flat -> Constants.FLAT
                    R.id.duplex -> Constants.DUPLEX
                    R.id.penthouse -> Constants.PENTHOUSE
                    else -> 0
                }
                FirebaseFunctions.updateHouse(
                        itemKey = act.houseSelected,
                        database = act.database,
                        agent = act.username,
                        area = areaField.text.toString().toInt(),
                        bathrooms = bathField.text.toString().toInt(),
                        bedrooms = bedField.text.toString().toInt(),
                        borough = boroughField.text.toString(),
                        description = descriptionField.text.toString(),
                        images = imageList.toFirebaseList(),
                        listDate = houseItem.listDate ?: "",
                        location = Functions.fullLocation(addressField.text.toString(), cityField.text.toString(), zipField.text.toString()),
                        price = priceField.text.toString().toInt(),
                        rooms = roomsField.text.toString().toInt(),
                        saleDate = "",
                        type = type
                )
            } else {
                Toast.makeText(context, resources.getString(R.string.fields_empty), Toast.LENGTH_LONG).show()
            }
        }
        addImageButton.setOnClickListener {
            if (imageRoomField.text.isNotEmpty() && imageUrlField.text.isNotEmpty()) {
                val image = MediaImage()
                image.room = imageRoomField.text.toString()
                image.url = imageUrlField.text.toString()
                imageList.add(image)
                imagesList.text = imageList.toTextList()
            }
        }
        removeImageButton.setOnClickListener {
            if (imageList.isNotEmpty()) {
                imageList.removeAt(imageList.lastIndex)
                imagesList.text = imageList.toTextList()
            }
        }
    }
}