package com.application.onlineTestSeries.Utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log

/**
 * Created by khalil on 21/2/17.
 */

class checkInternetState {
    internal lateinit var connectivityManager: ConnectivityManager
    internal var wifiInfo: NetworkInfo? = null
    internal var mobileInfo: NetworkInfo? = null
    internal var connected = false

    val isOnline: Boolean
        get() {
            try {
                connectivityManager = context
                        .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

                val networkInfo = connectivityManager.activeNetworkInfo
                connected = networkInfo != null && networkInfo.isAvailable &&
                        networkInfo.isConnected
                return connected


            } catch (e: Exception) {
                println("CheckConnectivity Exception: " + e.message)
                Log.v("connectivity", e.toString())
            }

            return connected
        }

    companion object {

        private val instance = checkInternetState()
        lateinit var context: Context

        fun getInstance(ctx: Context): checkInternetState {
            context = ctx.applicationContext
            return instance
        }
    }
}


