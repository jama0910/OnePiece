package no.kristiania.onepiece.utility

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

object Utils{
    fun isOnline(context: Context): Boolean {
        // This is deprecated since API29 - could not find a quick and easy replacement
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return networkInfo?.isConnected == true
    }
}

enum class StatusUpdate {
    NOOP,
    UPDATING,
    SUCCESS,
    ERROR
}