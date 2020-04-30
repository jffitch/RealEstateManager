package com.mathgeniusguide.realestatemanager.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mathgeniusguide.realestatemanager.MainActivity
import com.mathgeniusguide.realestatemanager.R
import com.mathgeniusguide.realestatemanager.database.HouseFirebaseItem
import com.mathgeniusguide.realestatemanager.objects.MediaImage
import com.mathgeniusguide.realestatemanager.utils.*
import kotlinx.android.synthetic.main.house_item.view.*

class HouseAdapter(private val items: ArrayList<HouseFirebaseItem>,
                   val context: Context,
                   val act: MainActivity,
                   private val surfaceStats: TextView,
                   private val roomsStats: TextView,
                   private val bathroomsStats: TextView,
                   private val bedroomsStats: TextView,
                   private val locationStats: TextView,
                   private val descriptionText: TextView,
                   private val imageList: RecyclerView,
                   private val priceStats: TextView,
                   private val listingStats: TextView,
                   private val googleMap: ImageView) : RecyclerView.Adapter<HouseAdapter.ViewHolder>() {
    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.house_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val i = items[position]
        val images: List<MediaImage> = if (i.images != null) i.images!!.split("||").map {it.toMediaImage ()} else emptyList()
        // if image exists, display first image in house list
        if (images.isNotEmpty()) {
            Glide.with(context).load(images[0].url).into(holder.houseImage)
        }
        // load information for each house into house list
        val currency = context.resources.getString(R.string.currency)
        holder.houseType.text = i.type!!.toHouseType(context.resources)
        holder.houseLocation.text = i.borough
        holder.housePrice.text = if (i.saleDate == "") String.format(context.resources.getString(R.string.dollar_sign), currency.format(i.price), currency.format(i.price?.convertDollarsToEuros())) else context.resources.getString(R.string.sold_caps)
        holder.parent.setOnClickListener {
            // when house is clicked, load information into main screen
            act.houseSelected = i.id ?: ""
            surfaceStats.text = String.format(context.resources.getString(R.string.number_sq_m), i.area)
            roomsStats.text = i.rooms.toString()
            bathroomsStats.text = i.bathrooms.toString()
            bedroomsStats.text = i.bedrooms.toString()
            locationStats.text = Regex("\\|").replace(i.location!!, "\n")
            descriptionText.text = i.description
            priceStats.text = String.format(context.resources.getString(R.string.dollar_sign), currency.format(i.price), "%,d".format(i.price?.convertDollarsToEuros()))
            val listedOn = String.format(context.resources.getString(R.string.listed_on), (i.listDate ?: "").toDateText())
            val listedBy = String.format(context.resources.getString(R.string.by_agent), i.agent)
            val soldOn = String.format(context.resources.getString(if (i.saleDate == "") R.string.not_sold else R.string.sold_on), (i.saleDate ?: "").toDateText())
            listingStats.text = String.format(context.resources.getString(R.string.full_listing), listedOn, listedBy, soldOn)
            imageList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            imageList.adapter = MediaAdapter(images as ArrayList<MediaImage>, context)
            Glide.with(context).load(i.location!!.toAPI(250,250,15)).into(googleMap)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val houseImage = view.houseImage
        val houseType = view.houseType
        val houseLocation = view.houseLocation
        val housePrice = view.housePrice
        val parent = view
    }
}