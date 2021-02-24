package com.example.media.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo





class NetworkUtils {
    companion object {
        fun isNetworkAvailable(context: Context): Boolean {
            val connectivity =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivity == null) {
                return false
            } else {
                val info = connectivity.allNetworkInfo
                for (networkInfo in info) {
                    if (networkInfo.state == NetworkInfo.State.CONNECTED) {
                        return true
                    }
                }
            }
            return false
        }
    }
}