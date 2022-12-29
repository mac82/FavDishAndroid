package com.softinsa.myapplication.views.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.softinsa.myapplication.database.repository.FavDishRepository
import com.softinsa.myapplication.views.viewmodel.FavDishViewModel
import java.lang.IllegalArgumentException

class FavDishViewModelFactory(private val repository: FavDishRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(FavDishViewModel::class.java)){
            return FavDishViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown View Model Class")
    }
}