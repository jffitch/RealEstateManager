package com.mathgeniusguide.realestatemanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mathgeniusguide.realestatemanager.MainActivity
import com.mathgeniusguide.realestatemanager.R
import kotlinx.android.synthetic.main.login_fragment.*

class LoginFragment: Fragment() {
    private lateinit var act: MainActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        act = activity as MainActivity
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.login_fragment, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        signInButton.setOnClickListener {
            if (usernameField.text.isNotEmpty() && passwordField.text.isNotEmpty()) {
                act.loginUser(usernameField.text.toString(), passwordField.text.toString())
            } else {
                Toast.makeText(context, "Username and password are required.", Toast.LENGTH_LONG).show()
            }
        }
        newAccountButton.setOnClickListener {
            if (usernameField.text.isNotEmpty() && passwordField.text.isNotEmpty()) {
                act.newUser(usernameField.text.toString(), passwordField.text.toString())
            } else {
                Toast.makeText(context, "Username and password are required.", Toast.LENGTH_LONG).show()
            }
        }
    }
}