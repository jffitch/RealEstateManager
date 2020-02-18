package com.mathgeniusguide.realestatemanager.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mathgeniusguide.realestatemanager.R
import com.mathgeniusguide.realestatemanager.objects.HouseItem
import com.mathgeniusguide.realestatemanager.objects.MediaImage
import com.mathgeniusguide.realestatemanager.utils.toHouseType
import com.mathgeniusguide.realestatemanager.utils.toMediaImage
import kotlinx.android.synthetic.main.house_item.view.*

class HouseAdapter(private val items: ArrayList<HouseItem>,
                   val context: Context,
                   val surfaceStats: TextView,
                   val roomsStats: TextView,
                   val bathroomsStats: TextView,
                   val bedroomsStats: TextView,
                   val locationStats: TextView,
                   val descriptionText: TextView,
                   val imageList: RecyclerView) : RecyclerView.Adapter<HouseAdapter.ViewHolder>() {
    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.house_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val i = items[position]
        val images: List<MediaImage> = if (i.images != null) i.images!!.split("||").map {it.toMediaImage ()} else emptyList()
        if (images.isNotEmpty()) {
            Glide.with(context).load(images[0].url).into(holder.houseImage)
        }
        holder.houseImage.visibility = if (true) View.VISIBLE else View.GONE
        holder.houseType.text = i.type!!.toHouseType(context.resources)
        holder.houseLocation.text = i.borough
        holder.housePrice.text = String.format(context.resources.getString(R.string.dollar_sign), "%,d".format(i.price))
        holder.parent.setOnClickListener {
            surfaceStats.text = String.format(context.resources.getString(R.string.number_sq_m), i.area)
            roomsStats.text = i.rooms.toString()
            bathroomsStats.text = i.bathrooms.toString()
            bedroomsStats.text = i.bedrooms.toString()
            locationStats.text = Regex("\\|").replace(i.location!!, "\n")
            descriptionText.text = i.description
            imageList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            imageList.adapter = MediaAdapter(images as ArrayList<MediaImage>, context)
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