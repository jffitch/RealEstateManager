package com.mathgeniusguide.realestatemanager.viewModel

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mathgeniusguide.realestatemanager.MainActivity
import com.mathgeniusguide.realestatemanager.R
import com.mathgeniusguide.realestatemanager.api.Api
import com.mathgeniusguide.realestatemanager.database.HouseDao
import com.mathgeniusguide.realestatemanager.database.HouseDatabase
import com.mathgeniusguide.realestatemanager.database.HouseFirebaseItem
import com.mathgeniusguide.realestatemanager.database.HouseRoomdbItem
import com.mathgeniusguide.realestatemanager.utils.ConnectivityInterceptor
import com.mathgeniusguide.realestatemanager.utils.Constants
import com.mathgeniusguide.realestatemanager.utils.NoConnectivityException
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch

class HousesViewModel(application: Application) : AndroidViewModel(application) {
    // declare MutableLiveData variables for use in this class
    private val _newHouseWithCoordinates: MutableLiveData<HouseFirebaseItem?>? = MutableLiveData()
    private val _updatedHouseWithCoordinates: MutableLiveData<HouseFirebaseItem?>? = MutableLiveData()
    private val _houseList = MutableLiveData<List<HouseRoomdbItem>>()

    // declare LiveData variables for observing in other classes
    val newHouseWithCoordinates: LiveData<HouseFirebaseItem?>?
        get() = _newHouseWithCoordinates
    val updatedHouseWithCoordinates: LiveData<HouseFirebaseItem?>?
        get() = _updatedHouseWithCoordinates
    val houseList: LiveData<List<HouseRoomdbItem>>
        get() = _houseList


    // Room Database
    private var db: HouseDatabase? = null
    private var dao: HouseDao? = null

    fun insertHouseItem(item: HouseRoomdbItem, context: Context) {
        // run function from DAO
        viewModelScope.launch {
            db = HouseDatabase.getDataBase(context)
            dao = db?.houseDao()
            Observable.fromCallable({
                dao?.insertHouseItem(item)
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
        }
    }

    fun fetchSavedHouses(context: Context) {
        // fetch all data from database
        viewModelScope.launch {
            db = HouseDatabase.getDataBase(context)
            dao = db?.houseDao()
            dao?.selectAllHouses()?.observeForever({
                _houseList.postValue(it)
            })
        }
    }

    // fetch coordinates for house address, then create or update on firebase
    fun fetchHouseCoordinates(house: HouseFirebaseItem, isNew: Boolean, context: Context) {
        val connectivityInterceptor = ConnectivityInterceptor(getApplication())
        viewModelScope.launch {
            try {
                Log.d("Real Estate Manager", "ViewModel Function Run")
                // fetch response from API, use result
                val response = Api.invoke(connectivityInterceptor).getHouseItemWithCoordinates(house.location ?: "").body()
                val result = response?.results
                if (!result.isNullOrEmpty()) {
                    // use location from first result, if exists
                    // update house information to include fetched latitude and longitude
                    // post to newHouseWithCoordinates if new house, to updatedHouseWithCoordinates if updated house
                    // newHouseWithCoordinates and updatedHouseWithCoordinates will be observed in MainActivity
                    val location = result[0].geometry.location
                    Log.d("Real Estate Manager", "ViewModel Result Not Empty")
                    house.latitude = location.lat
                    house.longitude = location.lng
                    if (isNew) {
                        _newHouseWithCoordinates?.postValue(house)
                    } else {
                        _updatedHouseWithCoordinates?.postValue(house)
                    }
                } else {
                    // if result is empty because API call failed due to invalid address, show error message
                    Toast.makeText(context, context.resources.getString(R.string.invalid_address), Toast.LENGTH_LONG).show()
                }
            } catch (e: NoConnectivityException) {
                // if no internet connection, show error message
                Toast.makeText(context, context.resources.getString(R.string.no_internet), Toast.LENGTH_LONG).show()
            }
        }
    }

    fun fetchCenterCoordinates(address: String, act: MainActivity) {
        val connectivityInterceptor = ConnectivityInterceptor(getApplication())
        viewModelScope.launch {
            try {
                // fetch response from API, use result
                val response = Api.invoke(connectivityInterceptor).getHouseItemWithCoordinates(address).body()
                val result = response?.results
                if (!result.isNullOrEmpty()) {
                    // use location from first result, if exists
                    val location = result[0].geometry.location
                    act.latitude.postValue(location.lat)
                    act.longitude.postValue(location.lng)
                } else {
                    // if result is empty because API call failed due to invalid address, set to New York center as default
                    act.latitude.postValue(Constants.LATITUDE_DEFAULT)
                    act.longitude.postValue(Constants.LONGITUDE_DEFAULT)
                }
            } catch (e: NoConnectivityException) {
                // if no internet connection, set to New York center as default
                act.latitude.postValue(Constants.LATITUDE_DEFAULT)
                act.longitude.postValue(Constants.LONGITUDE_DEFAULT)
            }
        }
    }
}