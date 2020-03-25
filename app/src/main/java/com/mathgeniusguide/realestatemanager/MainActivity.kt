package com.mathgeniusguide.realestatemanager

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

class MainActivity : AppCompatActivity() {
    val viewModel by lazy { ViewModelProviders.of(this).get(HousesViewModel::class.java) }
    lateinit var database: DatabaseReference
    var houseItemList = emptyList<HouseFirebaseItem>().toMutableList()
    var filteredHouseItemList = emptyList<HouseFirebaseItem>().toMutableList()
    val TAG = "Real Estate Manager"
    lateinit var navController: NavController
    val firebaseLoaded = MutableLiveData<Boolean>()
    val roomdbLoaded = MutableLiveData<Boolean>()
    private lateinit var auth: FirebaseAuth
    val ANONYMOUS = "anonymous"
    var username = ANONYMOUS
    var houseSelected = ""
    val markerList = emptyList<Marker?>().toMutableList()
    var screenWidth = 0

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
    }

    var itemListener: ValueEventListener = object : ValueEventListener {
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
            houseItem.agent = map.get("agent") as String?
            houseItem.area = (map.get("area") as Long).toInt()
            houseItem.bathrooms = (map.get("bathrooms") as Long).toInt()
            houseItem.bedrooms = (map.get("bedrooms") as Long).toInt()
            houseItem.borough = map.get("borough") as String?
            houseItem.description = map.get("description") as String?
            houseItem.images = map.get("images") as String?
            houseItem.latitude = map.get("latitude") as Double?
            houseItem.listDate = map.get("listDate") as String?
            houseItem.location = map.get("location") as String?
            houseItem.longitude = map.get("longitude") as Double?
            houseItem.price = (map.get("price") as Long).toInt()
            houseItem.rooms = (map.get("rooms") as Long).toInt()
            houseItem.saleDate = map.get("saleDate") as String?
            houseItem.type = (map.get("type") as Long).toInt()
            // if house with that ID already exists, remove it and replace with new house
            houseItemList = houseItemList.filter {houseItem.id != it.id}.toMutableList()
            houseItemList.add(houseItem)
            // for each house loaded from Firebase, add to Room Database
            viewModel.insertHouseItem(houseItem.toHouseRoomdbItem(), this)
        }
        firebaseLoaded.value = true
    }

    fun observeDatabase() {
        viewModel.houseList.observe(this, Observer {
            if (it != null) {
                for (i in it) {
                    if (houseItemList.none {house -> house.id == i.id}) {
                        houseItemList.add(i.toHouseFirebaseItem())
                    }
                }
                roomdbLoaded.postValue(true)
            }
        })
    }

    fun observeCoordinates() {
        viewModel.newHouseWithCoordinates?.observe(this, Observer {
            // whenever house is posted here, create house in Firebase
            if (it != null) {
                Log.d(TAG, "House Added")
                createHouse(it, database)
                sendNotification(it, this, true)
            }
        })
        viewModel.updatedHouseWithCoordinates?.observe(this, Observer {
            // whenever house is posted here, update house in Firebase
            if (it != null) {
                Log.d(TAG, "House Updated")
                updateHouse(it, database)
                sendNotification(it, this, false)
            }
        })
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

    fun login(user: FirebaseUser?) {
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

    fun updateDisplayName(user: FirebaseUser?, displayName: String) {
        if (user != null) {
            val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName).build();
            user.updateProfile(profileUpdates);
        }
    }
}
