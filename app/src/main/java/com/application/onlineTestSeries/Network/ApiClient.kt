package com.application.onlineTestSeries.Network

/**
 * Created by khalil on 6/3/18.
 */

import com.application.onlineTestSeries.Utils.UtilsApp
import com.google.gson.GsonBuilder

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    val BASE_URL = UtilsApp.webUrl
    private var retrofit: Retrofit? = null

    val client: Retrofit
        get() {

            val logging = HttpLoggingInterceptor()
            val httpClient = OkHttpClient.Builder().addInterceptor(logging).build()

            logging.level = Level.BODY

            if (retrofit == null) {
                val gson = GsonBuilder()
                        .setLenient()
                        .create()

                retrofit = Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .client(httpClient)
                        .build()
            }
            return retrofit!!
        }
}