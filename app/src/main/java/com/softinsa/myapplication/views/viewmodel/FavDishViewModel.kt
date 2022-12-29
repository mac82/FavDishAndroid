package com.softinsa.myapplication.views.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.softinsa.myapplication.Constants
import com.softinsa.myapplication.database.entities.FavDishEntity
import com.softinsa.myapplication.database.repository.FavDishRepository
import kotlinx.coroutines.launch

class FavDishViewModel(private val repository: FavDishRepository) : ViewModel() {


    fun insertDish(dishEntity: FavDishEntity) = viewModelScope.launch {
        repository.insertFavDishData(dishEntity)
    }

    val allDishesList: LiveData<List<FavDishEntity>> =
        repository.allDishesList.asLiveData()

    val favoriteDishesList: LiveData<List<FavDishEntity>> =
        repository.favoriteDishesList.asLiveData()

    fun updateDishDetails(favDishEntity: FavDishEntity) = viewModelScope.launch {
        repository.updateDishDetails(favDishEntity)
        Log.d(Constants.TAG, "Dish Fav Status Updated to Fav = ${favDishEntity.favoriteDish}")
    }

    fun deleteFavDish(favDishEntity: FavDishEntity) = viewModelScope.launch {
        repository.deleteFavDish(favDishEntity)
        Log.d(Constants.TAG, "Dish Deleted ID = ${favDishEntity.id}")
    }

    fun getFilteredList(value: String): LiveData<List<FavDishEntity>> =
        repository.filteredListDishes(value).asLiveData()

}