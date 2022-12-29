package com.softinsa.myapplication.database.dao

import androidx.room.*
import com.softinsa.myapplication.database.entities.FavDishEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavDishDao {

    @Insert
    suspend fun insertFavDishDetails(favDishEntity: FavDishEntity)

    @Query("SELECT * FROM FAV_DISHES_TABLE ORDER BY ID")
    fun getAllDishesList(): Flow<List<FavDishEntity>>

    @Query("SELECT * FROM FAV_DISHES_TABLE WHERE FAVORITE_DISH = 1")
    fun getFavoriteDishesList(): Flow<List<FavDishEntity>>

    @Update
    suspend fun updateFavDishDetails(favDishEntity: FavDishEntity)

    @Delete
    suspend fun deleteFavDish(favDishEntity: FavDishEntity)

    @Query("SELECT * FROM FAV_DISHES_TABLE WHERE type = :filterType")
    fun getFilteredDishList(filterType: String): Flow<List<FavDishEntity>>
}