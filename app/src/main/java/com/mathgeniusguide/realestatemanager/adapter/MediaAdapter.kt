package com.mathgeniusguide.realestatemanager.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mathgeniusguide.realestatemanager.R
import com.mathgeniusguide.realestatemanager.objects.MediaImage
import kotlinx.android.synthetic.main.media_item.view.*

class MediaAdapter (private val items: ArrayList<MediaImage>, val context: Context) : RecyclerView.Adapter<MediaAdapter.ViewHolder> () {
    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.media_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val i = items[position]
        // display image and corresponding room name for each item in media list
        Glide.with(context).load(i.url).into(holder.roomImage)
        holder.roomName.text = i.room
    }

    class ViewHolder (view : View) : RecyclerView.ViewHolder(view) {
        val roomImage = view.roomImage
        val roomName = view.roomName
        val parent = view
    }
}