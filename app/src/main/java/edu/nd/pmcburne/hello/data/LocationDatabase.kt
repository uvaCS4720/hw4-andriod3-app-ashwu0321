package edu.nd.pmcburne.hello.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [LocationEntity::class], version = 1)
@TypeConverters(ListConverter::class)
abstract class LocationDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao

    companion object {
        @Volatile
        private var instance: LocationDatabase? = null

        fun getInstance(context: Context): LocationDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    LocationDatabase::class.java,
                    "location_database"
                ).build().also { instance = it }
            }
        }
    }
}
