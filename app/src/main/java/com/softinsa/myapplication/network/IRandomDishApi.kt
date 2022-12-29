package com.softinsa.myapplication.network

import com.softinsa.myapplication.BuildConfig
import com.softinsa.myapplication.Constants
import com.softinsa.myapplication.model.RandomDishModel
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface IRandomDishApi {

    @GET(Constants.API_ENDPOINT)
    fun getRandomDish(
        @Query(Constants.API_KEY_QUERY) apiKey: String,
        @Query(Constants.LIMIT_LICENSE) limitLicense: Boolean,
        @Query(Constants.TAGS) tags: String,
        @Query(Constants.NUMBER) number: Int
    ): Single<RandomDishModel.Recipes>

}
/*
    @GET(Constants.API_ENDPOINT)
    fun getRandomDish(
        // Query parameter appended to the URL. This is the best practice instead of appending it as we have done in the browser.
        @Query(Constants.API_KEY) apiKey: String,
        @Query(Constants.LIMIT_LICENSE) limitLicense: Boolean,
        @Query(Constants.TAGS) tags: String,
        @Query(Constants.NUMBER) number: Int
    ): Single<RandomDish.Recipes> // The Single class implements the Reactive Pattern for a single value response. Click on the class using the Ctrl + Left Mouse Click to know more.
}

 */