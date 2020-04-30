package com.mathgeniusguide.realestatemanager.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mathgeniusguide.realestatemanager.R
import com.mathgeniusguide.realestatemanager.objects.PaymentItem
import kotlinx.android.synthetic.main.payment_item.view.*

class PaymentAdapter(private val items: ArrayList<PaymentItem>, val context: Context) : RecyclerView.Adapter<PaymentAdapter.ViewHolder>() {
    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.payment_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val i = items[position]
        // load payment info into each item in payment list
        holder.paymentDate.text = i.date
        holder.paymentPrincipal.text = i.principal
        holder.paymentInterest.text = i.interest
        holder.paymentBalance.text = i.balance
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val paymentDate = view.paymentDate
        val paymentPrincipal = view.paymentPrincipal
        val paymentInterest = view.paymentInterest
        val paymentBalance = view.paymentBalance
        val parent = view
    }
}