package com.mathgeniusguide.realestatemanager.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mathgeniusguide.realestatemanager.MainActivity
import com.mathgeniusguide.realestatemanager.R
import com.mathgeniusguide.realestatemanager.utils.Functions.filled
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.login_fragment.*

class LoginFragment : Fragment() {
    private lateinit var act: MainActivity
    var newAccountReady = false

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
        act.toolbar.visibility = View.VISIBLE
        act.toolbar.navigationIcon = null
        signInButton.setOnClickListener {
            // Sign In button clicked
            // if email and password are filled, sign in
            // otherwise, show error message
            if (filled(emailField, passwordField)) {
                act.loginUser(emailField.text.toString(), passwordField.text.toString())
            } else {
                Toast.makeText(context, resources.getString(R.string.email_password_required), Toast.LENGTH_LONG).show()
            }
        }
        newAccountButton.setOnClickListener {
            // New Account button clicked
            // if clicked for first time, make additional fields visible
            // otherwise, if fields are filled and passwords match, create new user
            // if fields aren't filled or passwords don't match, show error message
            if (newAccountReady) {
                if (filled(emailField, displayNameField, passwordField, reenterPasswordField)) {
                    if (passwordField.text.toString() == reenterPasswordField.text.toString()) {
                        act.newUser(emailField.text.toString(), passwordField.text.toString(), displayNameField.text.toString())
                    } else {
                        Toast.makeText(context, resources.getString(R.string.passwords_must_match), Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(context, resources.getString(R.string.email_displayname_password_reenter_required), Toast.LENGTH_LONG).show()
                }
            } else {
                displayNameLabel.visibility = View.VISIBLE
                displayNameField.visibility = View.VISIBLE
                reenterPasswordLabel.visibility = View.VISIBLE
                reenterPasswordField.visibility = View.VISIBLE
                newAccountReady = true
            }
        }
    }
}