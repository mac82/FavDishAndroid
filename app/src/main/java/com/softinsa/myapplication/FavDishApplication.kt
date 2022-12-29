package com.softinsa.myapplication

import android.app.Application
import com.softinsa.myapplication.database.FavDishDatabase
import com.softinsa.myapplication.database.repository.FavDishRepository

class FavDishApplication: Application() {

    private val database by lazy { FavDishDatabase.getDatabase(this) }

    val repository by lazy { FavDishRepository(database.favDishDao()) }
}