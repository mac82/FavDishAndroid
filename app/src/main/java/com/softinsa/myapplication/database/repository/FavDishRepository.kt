package com.softinsa.myapplication.database.repository

import androidx.annotation.WorkerThread
import com.softinsa.myapplication.database.dao.FavDishDao
import com.softinsa.myapplication.database.entities.FavDishEntity
import kotlinx.coroutines.flow.Flow

class FavDishRepository(private val favDishDao: FavDishDao) {

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertFavDishData(favDishEntity: FavDishEntity){
        favDishDao.insertFavDishDetails(favDishEntity)
    }

    val allDishesList: Flow<List<FavDishEntity>> = favDishDao.getAllDishesList()

    val favoriteDishesList: Flow<List<FavDishEntity>> = favDishDao.getFavoriteDishesList()

    @WorkerThread
    suspend fun updateDishDetails(favDishEntity: FavDishEntity) {
        favDishDao.updateFavDishDetails(favDishEntity)
    }

    @WorkerThread
    suspend fun deleteFavDish(favDishEntity: FavDishEntity){
        favDishDao.deleteFavDish(favDishEntity)
    }

    fun filteredListDishes(value: String): Flow<List<FavDishEntity>> =
        favDishDao.getFilteredDishList(value)


    // FROM API
    /*private val randomDishService = RandomDishService(RetrofitBuilder.randomDishApiService)

    fun getRandomDish(): Single<RandomDishModel.Recipes> = randomDishService.getRandomDish()*/
}