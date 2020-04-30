package com.mathgeniusguide.realestatemanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mathgeniusguide.realestatemanager.MainActivity
import com.mathgeniusguide.realestatemanager.R
import com.mathgeniusguide.realestatemanager.adapter.PaymentAdapter
import com.mathgeniusguide.realestatemanager.objects.PaymentItem
import com.mathgeniusguide.realestatemanager.utils.convertDollarsToEuros
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.loan_simulator_fragment.*
import java.text.DateFormatSymbols
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.*

class LoanSimulatorFragment : Fragment() {
    lateinit var act: MainActivity
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.loan_simulator_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        act = activity as MainActivity
        act.toolbar.visibility = View.VISIBLE
        act.toolbar.navigationIcon = null
        val house = act.houseItemList.first { it.id == act.houseSelected }
        val price = if (Locale.getDefault().language != "en") house.price?.convertDollarsToEuros() ?: 0 else house.price ?: 0
        priceField.text = "%,d".format(price)
        paymentList.layoutManager = LinearLayoutManager(context)

        calculateButton.setOnClickListener {
            if (timeField.text.isEmpty()) {
                Toast.makeText(context, resources.getString(R.string.time_empty), Toast.LENGTH_LONG).show()
            } else {
                var balance = max(price.toDouble() - if (downPaymentField.text.isEmpty()) 0.0 else downPaymentField.text.toString().toDouble(), 0.0)
                val rate = if (interestField.text.isEmpty()) 1.0 else exp(interestField.text.toString().toDouble() / 1200)
                val time = timeField.text.toString().toDouble() * 12
                val payment = if (rate == 1.0) balance / time else rate.pow(time) * balance * (rate - 1) / (rate.pow(time) - 1)
                val currency = resources.getString(R.string.currency_decimal)
                paymentField.text = currency.format(payment)

                val firstItem = PaymentItem()
                firstItem.balance = String.format(context!!.resources.getString(R.string.dollar_sign), currency.format(balance), currency.format(balance))
                val itemList = ArrayList<PaymentItem>()
                itemList.add(firstItem)
                val calendar = Calendar.getInstance()
                calendar.time = Date()
                val monthNames = DateFormatSymbols(Locale.getDefault()).months
                var month = calendar.get(Calendar.MONTH)
                var year = calendar.get(Calendar.YEAR)
                while (balance > 0) {
                    val paymentItem = PaymentItem()
                    val interest = floor(balance * (rate - 1) * 100) / 100
                    val principal = min(payment - interest, balance)
                    balance -= principal
                    month++
                    if (month >= 12) {
                        month = 0
                        year++
                    }
                    paymentItem.date = String.format(resources.getString(R.string.date_text), monthNames[month], year)
                    paymentItem.principal = String.format(context!!.resources.getString(R.string.dollar_sign), "%,.2f".format(principal), "%,.2f".format(principal))
                    paymentItem.interest = String.format(context!!.resources.getString(R.string.dollar_sign), "%,.2f".format(interest), "%,.2f".format(interest))
                    paymentItem.balance = String.format(context!!.resources.getString(R.string.dollar_sign), "%,.2f".format(balance), "%,.2f".format(balance))
                    itemList.add(paymentItem)
                }
                paymentList.adapter = PaymentAdapter(itemList, context!!)
            }
        }
    }
}