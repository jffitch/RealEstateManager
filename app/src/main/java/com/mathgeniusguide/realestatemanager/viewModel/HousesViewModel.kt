package com.mathgeniusguide.realestatemanager.viewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mathgeniusguide.realestatemanager.api.Api
import com.mathgeniusguide.realestatemanager.database.HouseDao
import com.mathgeniusguide.realestatemanager.database.HouseDatabase
import com.mathgeniusguide.realestatemanager.database.HouseFirebaseItem
import com.mathgeniusguide.realestatemanager.database.HouseRoomdbItem
import com.mathgeniusguide.realestatemanager.utils.ConnectivityInterceptor
import com.mathgeniusguide.realestatemanager.utils.NoConnectivityException
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch

class HousesViewModel(application: Application) : AndroidViewModel(application) {
    // declare MutableLiveData variables for use in this class
    private val _houseList = MutableLiveData<List<HouseRoomdbItem>>()

    // declare LiveData variables for observing in other classes
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

    // declare MutableLiveData variables for use in this class
    private val _houseItemWithCoordinates: MutableLiveData<HouseFirebaseItem?>? = MutableLiveData()
    private val _isDataLoading = MutableLiveData<Boolean>()
    private val _isDataLoadingError = MutableLiveData<Boolean>()

    // declare LiveData variables for observing in other classes
    val houseItemWithCoordinates: LiveData<HouseFirebaseItem?>?
        get() = _houseItemWithCoordinates
    val isDataLoading: LiveData<Boolean>
        get() = _isDataLoading
    val isDataLoadingError: LiveData<Boolean>
        get() = _isDataLoadingError

    // fetch variables
    fun fetchHouseItemWithCoordinates(house: HouseFirebaseItem) {
        val connectivityInterceptor = ConnectivityInterceptor(getApplication())
        _isDataLoading.value = true
        viewModelScope.launch {
            try {
                _houseItemWithCoordinates?.postValue(Api.invoke(connectivityInterceptor).getHouseItemWithCoordinates(house.location ?: "").body())
                _isDataLoading.postValue(false)
                _isDataLoadingError.postValue(false)
            } catch (e: NoConnectivityException) {
                _isDataLoading.postValue(false)
                _isDataLoadingError.postValue(true)
            }
        }
    }
}