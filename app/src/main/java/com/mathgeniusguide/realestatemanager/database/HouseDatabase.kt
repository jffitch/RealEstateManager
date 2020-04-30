package com.mathgeniusguide.realestatemanager.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [HouseRoomdbItem::class], version = 1, exportSchema = false)
abstract class HouseDatabase : RoomDatabase() {
    abstract fun houseDao(): HouseDao

    companion object {
        private var INSTANCE: HouseDatabase? = null
        var TEST_MODE = false

        fun getDataBase(context: Context): HouseDatabase? {
            if (INSTANCE == null) {
                synchronized(HouseDatabase::class) {
                    if (TEST_MODE) {
                        INSTANCE = Room.inMemoryDatabaseBuilder(context.applicationContext, HouseDatabase::class.java)
                                .allowMainThreadQueries()
                                .build()
                    } else {
                        INSTANCE = Room.databaseBuilder(context.applicationContext, HouseDatabase::class.java, "myDB")
                                .build()
                    }
                }
            }
            return INSTANCE
        }

        fun destroyDataBase() {
            INSTANCE = null
        }
    }
}