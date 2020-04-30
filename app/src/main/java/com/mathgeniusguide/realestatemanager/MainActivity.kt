package com.mathgeniusguide.realestatemanager

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.maps.model.Marker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.*
import com.mathgeniusguide.realestatemanager.database.HouseFirebaseItem
import com.mathgeniusguide.realestatemanager.utils.Constants
import com.mathgeniusguide.realestatemanager.utils.FirebaseFunctions.createHouse
import com.mathgeniusguide.realestatemanager.utils.FirebaseFunctions.updateHouse
import com.mathgeniusguide.realestatemanager.utils.Functions.sendNotification
import com.mathgeniusguide.realestatemanager.utils.toHouseFirebaseItem
import com.mathgeniusguide.realestatemanager.utils.toHouseRoomdbItem
import com.mathgeniusguide.realestatemanager.viewModel.HousesViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), LocationListener {
    private val viewModel by lazy { ViewModelProviders.of(this).get(HousesViewModel::class.java) }
    lateinit var database: DatabaseReference
    var houseItemList = emptyList<HouseFirebaseItem>().toMutableList()
    var filteredHouseItemList = emptyList<HouseFirebaseItem>().toMutableList()
    val TAG = "Real Estate Manager"
    private lateinit var navController: NavController
    val firebaseLoaded = MutableLiveData<Boolean>()
    val roomdbLoaded = MutableLiveData<Boolean>()
    private lateinit var auth: FirebaseAuth
    private val ANONYMOUS = "anonymous"
    var username = ANONYMOUS
    var houseSelected = ""
    val markerList = emptyList<Marker?>().toMutableList()
    var screenWidth = 0

    // Location
    private var locationEnabled = Build.VERSION.SDK_INT < Build.VERSION_CODES.M
    var latitude = MutableLiveData<Double>()
    var longitude = MutableLiveData<Double>()
    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        houseSelected = intent.getStringExtra("house") ?: ""

        navController = findNavController(nav_host_fragment)
        toolbar.setupWithNavController(navController)

        firebaseLoaded.value = false
        roomdbLoaded.value = false

        database = FirebaseDatabase.getInstance().reference
        database.orderByKey().addListenerForSingleValueEvent(itemListener)
        viewModel.fetchSavedHouses(this)
        observeDatabase()

        auth = FirebaseAuth.getInstance()
        login(auth.currentUser)
        observeCoordinates()

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        screenWidth = (displayMetrics.widthPixels / displayMetrics.density).toInt()
        Log.d(TAG, "Screen Width: " + screenWidth.toString())
        setUpLocation()
    }

    private var itemListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // Get Post object and use the values to update the UI
            addDataToList(dataSnapshot)
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Getting Item failed, log a message
            Log.w(TAG, "loadItem:onCancelled", databaseError.toException())
        }
    }

    private fun addDataToList(dataSnapshot: DataSnapshot) {
        val houses = dataSnapshot.child(Constants.HOUSES).children.iterator()
        while (houses.hasNext()) {
            val currentItem = houses.next()
            val houseItem = HouseFirebaseItem.create()
            val map = currentItem.getValue() as HashMap<String, Any>
            houseItem.id = currentItem.key
            houseItem.agent = map["agent"] as String?
            houseItem.area = (map["area"] as Long).toInt()
            houseItem.bathrooms = (map["bathrooms"] as Long).toInt()
            houseItem.bedrooms = (map["bedrooms"] as Long).toInt()
            houseItem.borough = map["borough"] as String?
            houseItem.description = map["description"] as String?
            houseItem.images = map["images"] as String?
            houseItem.latitude = map["latitude"] as Double?
            houseItem.listDate = map["listDate"] as String?
            houseItem.location = map["location"] as String?
            houseItem.longitude = map["longitude"] as Double?
            houseItem.price = (map["price"] as Long).toInt()
            houseItem.rooms = (map["rooms"] as Long).toInt()
            houseItem.saleDate = map["saleDate"] as String?
            houseItem.type = (map["type"] as Long).toInt()
            // if house with that ID already exists, remove it and replace with new house
            houseItemList = houseItemList.filter { houseItem.id != it.id }.toMutableList()
            houseItemList.add(houseItem)
            // for each house loaded from Firebase, add to Room Database
            viewModel.insertHouseItem(houseItem.toHouseRoomdbItem(), this)
        }
        firebaseLoaded.value = true
        houseItemList.sortBy { it.id }
    }

    private fun observeDatabase() {
        viewModel.houseList.observe(this, Observer {
            if (it != null) {
                for (i in it) {
                    if (houseItemList.none { house -> house.id == i.id }) {
                        houseItemList.add(i.toHouseFirebaseItem())
                    }
                }
                roomdbLoaded.postValue(true)
            }
        })
    }

    private fun observeCoordinates() {
        viewModel.newHouseWithCoordinates?.observe(this, Observer {
            // whenever house is posted here, create house in Firebase
            if (it != null) {
                Log.d(TAG, "House Added")
                createHouse(it, database, this)
                sendNotification(it, this, true)
            }
        })
        viewModel.updatedHouseWithCoordinates?.observe(this, Observer {
            // whenever house is posted here, update house in Firebase
            if (it != null) {
                Log.d(TAG, "House Updated")
                updateHouse(it, database, this)
                sendNotification(it, this, false)
            }
        })
    }

    private fun setUpLocation() {
        // load saved preference for radius
        val pref = getSharedPreferences(Constants.PREF, 0)
        val locationMethod = pref?.getInt("locationMethod", Constants.LOCATION_ACTUAL)
                ?: Constants.LOCATION_ACTUAL
        when (locationMethod) {
            Constants.LOCATION_ACTUAL -> {
                // initialize location to impossible values
                // location will be set later in this function
                latitude.postValue(91.0)
                longitude.postValue(181.0)
            }
            Constants.LOCATION_COORDINATES -> {
                // get coordinates from preferences, then exit this function
                latitude.postValue((pref?.getFloat("latitude", Constants.LATITUDE_DEFAULT.toFloat())
                        ?: Constants.LATITUDE_DEFAULT.toFloat()).toDouble())
                longitude.postValue((pref?.getFloat("longitude", Constants.LONGITUDE_DEFAULT.toFloat())
                        ?: Constants.LONGITUDE_DEFAULT.toFloat()).toDouble())
                return
            }
            Constants.LOCATION_ADDRESS -> {
                // get address from preferences, then exit this function
                viewModel.fetchCenterCoordinates(pref?.getString("address", Constants.ADDRESS_DEFAULT)
                        ?: Constants.ADDRESS_DEFAULT, this)
                return
            }
        }


        // request location permissions
        if (!locationPermission()) {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    123
            )
        }

        // activate location functionality if permission is granted
        if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationEnabled = true
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0,
                    0.toFloat(),
                    this
            )
        }
    }

    private fun locationPermission(): Boolean {
        // check whether permissions have been granted
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onLocationChanged(location: Location?) {
        // if location is not null and latitude and longitude have not been set, set latitude and longitude
        if (location != null && (latitude.value == 91.0 || longitude.value == 181.0)) {
            latitude.postValue(location.latitude)
            longitude.postValue(location.longitude)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_bar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // if user is not logged in, only login screen and instructions screen should be accessible
        if (username == ANONYMOUS && item.itemId != R.id.instructions && item.itemId != R.id.main) {
            return true
        }
        // if logout button is clicked, log out
        if (item.itemId == R.id.logout) {
            login(null)
            return true
        }
        // if edit button is clicked, edit screen should only be accessible if a property is selected and if the user is the agent who added that property
        if (item.itemId == R.id.edit) {
            if (houseSelected.isEmpty() || houseItemList.none { it.id == houseSelected }) {
                Toast.makeText(this, resources.getString(R.string.no_property_selected), Toast.LENGTH_LONG).show()
                return true
            }
            if (houseItemList.first { it.id == houseSelected }.agent != username) {
                Toast.makeText(this, resources.getString(R.string.not_agent), Toast.LENGTH_LONG).show()
                return true
            }
        }
        // if main button is clicked, go to login screen if user is not logged in
        // if user is logged in, clear houseSelected and filteredHouseItemList to show defaults
        if (item.itemId == R.id.main) {
            if (username == ANONYMOUS) {
                navController.navigate(R.id.action_logout)
                return true
            }
            filteredHouseItemList.clear()
        }
        // otherwise, load target fragment as normal
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    private fun login(user: FirebaseUser?) {
        // login(null) logs out
        // login(user) logs in that user
        if (user != null) {
            username = user.displayName ?: ANONYMOUS
            navController.navigate(R.id.action_login)
        } else {
            username = ANONYMOUS
            auth.signOut()
            navController.navigate(R.id.action_logout)
        }
    }

    fun newUser(email: String, password: String, displayName: String) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")
                        val user = auth.currentUser
                        updateDisplayName(user, displayName)
                        login(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                    }
                }
    }

    fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        val user = auth.currentUser
                        login(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                    }
                }
    }

    fun passwordReset(email: String) {
        if (email.isEmpty()) {
            Toast.makeText(this, resources.getString(R.string.email_required), Toast.LENGTH_LONG).show()
            return
        }
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    val message = resources.getString(if (task.isSuccessful) R.string.password_reset_email_sent else R.string.invalid_email)
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                }
    }

    private fun updateDisplayName(user: FirebaseUser?, displayName: String) {
        if (user != null) {
            val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName).build()
            user.updateProfile(profileUpdates)
        }
    }

    override fun onProviderDisabled(provider: String?) {
    }

    override fun onProviderEnabled(provider: String?) {
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }
}
