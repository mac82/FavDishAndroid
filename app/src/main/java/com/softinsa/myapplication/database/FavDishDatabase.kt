package com.softinsa.myapplication.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.softinsa.myapplication.database.dao.FavDishDao
import com.softinsa.myapplication.database.entities.FavDishEntity

@Database(
    entities = [FavDishEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FavDishDatabase: RoomDatabase() {

    abstract fun favDishDao(): FavDishDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: FavDishDatabase? = null

        fun getDatabase(context: Context): FavDishDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FavDishDatabase::class.java,
                    "fav_dish_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}