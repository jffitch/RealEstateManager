package com.mathgeniusguide.realestatemanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.mathgeniusguide.realestatemanager.objects.HouseItem
import com.mathgeniusguide.realestatemanager.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var database: DatabaseReference
    var houseItemList = emptyList<HouseItem>().toMutableList()
    val TAG = "Real Estate Manager"
    lateinit var navController: NavController
    val firebaseLoaded = MutableLiveData<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        navController = findNavController(nav_host_fragment)
        toolbar.setupWithNavController(navController)

        firebaseLoaded.value = false
        database = FirebaseDatabase.getInstance().reference
        database.orderByKey().addListenerForSingleValueEvent(itemListener)
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
            val houseItem = HouseItem.create()
            val map = currentItem.getValue() as HashMap<String, Any>
            houseItem.id = currentItem.key
            houseItem.area = (map.get("area") as Long).toInt()
            houseItem.bathrooms = (map.get("bathrooms") as Long).toInt()
            houseItem.bedrooms = (map.get("bedrooms") as Long).toInt()
            houseItem.borough = map.get("borough") as String?
            houseItem.description = map.get("description") as String?
            houseItem.images = map.get("images") as String?
            houseItem.location = map.get("location") as String?
            houseItem.price = (map.get("price") as Long).toInt()
            houseItem.rooms = (map.get("rooms") as Long).toInt()
            houseItem.type = (map.get("type") as Long).toInt()
            houseItemList.add(houseItem)
        }
        firebaseLoaded.value = true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_bar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

}
