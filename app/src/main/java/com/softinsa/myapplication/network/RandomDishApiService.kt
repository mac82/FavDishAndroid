package com.softinsa.myapplication.network

import android.app.backup.BackupAgentHelper
import com.softinsa.myapplication.BuildConfig
import com.softinsa.myapplication.Constants
import com.softinsa.myapplication.model.RandomDishModel
import io.reactivex.rxjava3.core.Single

class RandomDishApiService(private  val randomDishApiService: IRandomDishApi) {

    fun getRandomDish(): Single<RandomDishModel.Recipes> {
        return randomDishApiService.getRandomDish(
            BuildConfig.API_KEY,
            Constants.LIMIT_LICENSE_VALUE,
            Constants.TAGS_VALUE,
            Constants.NUMBER_VALUE
        )
    }
}


/*

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClientInstance {

    lateinit var retrofit: Retrofit

    private const val BASE_URL = "http://10.0.2.2:8080/"

    private var token = ""

    val retrofitInstance: Retrofit
        get() {
            if (!this::retrofit.isInitialized) {
                val headersInterceptor = Interceptor { chain ->
                    val requestBuilder = chain.request().newBuilder()
                    requestBuilder.header("Authorization", "Bearer $token")
                    chain.proceed(requestBuilder.build())
                }
                val okHttpClient = OkHttpClient()
                    .newBuilder()
                    .followRedirects(true)
                    .addInterceptor(headersInterceptor)
                    .build()
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .client(okHttpClient)
                    .build()
            }
            return retrofit
        }

    fun setToken(token: String) {
        RetrofitClientInstance.token = token
    }
}

 */