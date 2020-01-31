package com.mathgeniusguide.realestatemanager.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mathgeniusguide.realestatemanager.R
import com.mathgeniusguide.realestatemanager.objects.HouseItem
import com.mathgeniusguide.realestatemanager.utils.toHouseType
import kotlinx.android.synthetic.main.house_item.view.*

class HouseAdapter (private val items: ArrayList<HouseItem>, val context: Context) : RecyclerView.Adapter<HouseAdapter.ViewHolder> () {
    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.house_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val i = items[position]
        Glide.with(context).load(i.images!!.split(",")[0]).into(holder.houseImage)
        holder.houseImage.visibility = if (true) View.VISIBLE else View.GONE
        holder.houseType.text = i.type!!.toHouseType(context.resources)
        holder.houseLocation.text = i.borough
        holder.housePrice.text = "%,d".format(i.price)
        holder.parent.setOnClickListener {

        }
    }

    class ViewHolder (view : View) : RecyclerView.ViewHolder(view) {
        val houseImage = view.houseImage
        val houseType = view.houseType
        val houseLocation = view.houseLocation
        val housePrice = view.housePrice
        val parent = view
    }
}