package com.mathgeniusguide.realestatemanager.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface HouseDao{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertHouseItemIfNotExists(houseRoomdbItem: HouseRoomdbItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHouseItem(houseRoomdbItem: HouseRoomdbItem)

    @Update
    fun updateHouseItem(houseRoomdbItem: HouseRoomdbItem)

    @Delete
    fun deleteHouseItem(houseRoomdbItem: HouseRoomdbItem)

    @Query("SELECT * FROM HouseRoomdbItem")
    fun selectAllHouses(): LiveData<List<HouseRoomdbItem>>
}